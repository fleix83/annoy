package com.example.annoy.service

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.annoy.data.SettingsRepository
import com.example.annoy.data.UserSettings
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DeterrentService : LifecycleService() {

    private lateinit var repository: SettingsRepository
    private lateinit var player: DeterrentPlayer
    private lateinit var scheduleManager: ScheduleManager
    private lateinit var pauseManager: PauseManager
    private var screenReceiver: ScreenStateReceiver? = null

    private val handler = Handler(Looper.getMainLooper())
    private var deterrentRunnable: Runnable? = null
    private var graceRunnable: Runnable? = null
    private var isScreenOn = true
    private var currentSettings = UserSettings()
    private var settingsJob: Job? = null
    private var screenOnTimestamp = 0L
    private var currentEscalationLevel = 0

    override fun onCreate() {
        super.onCreate()
        repository = SettingsRepository(applicationContext)
        player = DeterrentPlayer(applicationContext)
        scheduleManager = ScheduleManager(applicationContext)
        pauseManager = PauseManager(repository, lifecycleScope)

        NotificationHelper.createChannel(this)

        screenReceiver = ScreenStateReceiver(
            onScreenOn = { onScreenOn() },
            onScreenOff = { onScreenOff() }
        )
        registerReceiver(screenReceiver, ScreenStateReceiver.getIntentFilter())

        settingsJob = lifecycleScope.launch {
            repository.settingsFlow.collectLatest { settings ->
                val oldSettings = currentSettings
                currentSettings = settings
                scheduleManager.updateAlarms(settings)
                updateNotification()

                if (isScreenOn && oldSettings != settings) {
                    cancelDeterrentCycle()
                    startGracePeriod()
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {
            NotificationHelper.ACTION_PAUSE -> {
                if (currentSettings.isPaused) {
                    pauseManager.resume()
                } else {
                    pauseManager.pause(currentSettings.pauseDefaultMinutes)
                }
            }
            NotificationHelper.ACTION_STOP -> {
                lifecycleScope.launch {
                    repository.setMasterEnabled(false)
                }
                stopSelf()
                return START_NOT_STICKY
            }
        }

        startForeground(
            NotificationHelper.NOTIFICATION_ID,
            NotificationHelper.buildNotification(this, getStatusText(), currentSettings.isPaused)
        )

        return START_STICKY
    }

    override fun onDestroy() {
        cancelDeterrentCycle()
        cancelGracePeriod()
        screenReceiver?.let { unregisterReceiver(it) }
        screenReceiver = null
        scheduleManager.cancelAlarms()
        player.release()
        settingsJob?.cancel()
        super.onDestroy()
    }

    private fun onScreenOn() {
        isScreenOn = true
        screenOnTimestamp = System.currentTimeMillis()
        currentEscalationLevel = 0
        startGracePeriod()
    }

    private fun onScreenOff() {
        isScreenOn = false
        screenOnTimestamp = 0L
        currentEscalationLevel = 0
        cancelGracePeriod()
        cancelDeterrentCycle()
        player.stopCurrentSound()
    }

    private fun startGracePeriod() {
        cancelGracePeriod()
        cancelDeterrentCycle()
        player.stopCurrentSound()

        if (!shouldPlayDeterrent()) return

        val graceMs = currentSettings.gracePeriodSeconds * 1000L
        if (graceMs <= 0) {
            startDeterrentCycle()
            return
        }

        graceRunnable = Runnable { startDeterrentCycle() }
        handler.postDelayed(graceRunnable!!, graceMs)
    }

    private fun startDeterrentCycle() {
        if (!shouldPlayDeterrent()) return

        playCurrentLevel()
        scheduleNextDeterrent()
    }

    private fun playCurrentLevel() {
        val level = getEscalationLevel()
        currentEscalationLevel = level

        if (level == 0) {
            // Pre-escalation: play the user's selected sound
            player.play(
                currentSettings.soundSelection,
                currentSettings.vibrationPattern,
                currentSettings.deterrentMode,
                currentSettings.volumePercent
            )
        } else {
            // Escalation: play escalation pattern using the selected sound
            player.playEscalation(
                level,
                currentSettings.soundSelection,
                currentSettings.vibrationPattern,
                currentSettings.deterrentMode,
                currentSettings.volumePercent
            )
        }
    }

    private fun scheduleNextDeterrent() {
        cancelDeterrentCycle()

        val level = getEscalationLevel()
        val intervalMs = when {
            level >= 4 -> 5500L    // continuous tone is 5s, small gap then replay
            level >= 3 -> 5500L    // rapid beeps is 5s, small gap then replay
            else -> currentSettings.intervalSeconds * 1000L
        }

        deterrentRunnable = Runnable {
            if (isScreenOn && shouldPlayDeterrent()) {
                playCurrentLevel()
                scheduleNextDeterrent()
            }
        }
        handler.postDelayed(deterrentRunnable!!, intervalMs)
    }

    private fun getEscalationLevel(): Int {
        if (screenOnTimestamp == 0L) return 0
        val elapsed = System.currentTimeMillis() - screenOnTimestamp
        return when {
            elapsed >= 4 * 60_000 -> 4  // 4+ min: continuous tone
            elapsed >= 3 * 60_000 -> 3  // 3-4 min: rapid beeps
            elapsed >= 2 * 60_000 -> 2  // 2-3 min: beep..beeeeep
            elapsed >= 1 * 60_000 -> 1  // 1-2 min: beep..beep
            else -> 0                    // 0-1 min: normal deterrent
        }
    }

    private fun shouldPlayDeterrent(): Boolean {
        if (!currentSettings.masterEnabled) return false
        if (currentSettings.isPaused) return false
        if (!scheduleManager.isInActiveWindow(currentSettings)) return false
        return true
    }

    private fun cancelGracePeriod() {
        graceRunnable?.let { handler.removeCallbacks(it) }
        graceRunnable = null
    }

    private fun cancelDeterrentCycle() {
        deterrentRunnable?.let { handler.removeCallbacks(it) }
        deterrentRunnable = null
    }

    private fun updateNotification() {
        NotificationHelper.update(this, getStatusText(), currentSettings.isPaused)
    }

    private fun getStatusText(): String {
        if (currentSettings.isPaused) {
            val mins = (currentSettings.pauseRemainingMillis / 60_000) + 1
            return "Paused \u2014 ${mins}m remaining"
        }
        if (currentSettings.scheduleMode == "scheduled" && !scheduleManager.isInActiveWindow(currentSettings)) {
            val h = currentSettings.scheduleStartHour.toString().padStart(2, '0')
            val m = currentSettings.scheduleStartMinute.toString().padStart(2, '0')
            return "Scheduled \u2014 resumes at $h:$m"
        }
        return "Active"
    }
}
