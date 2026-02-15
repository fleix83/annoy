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

                // If settings changed while screen is on, restart the cycle
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
        startGracePeriod()
    }

    private fun onScreenOff() {
        isScreenOn = false
        cancelGracePeriod()
        cancelDeterrentCycle()
    }

    private fun startGracePeriod() {
        cancelGracePeriod()
        cancelDeterrentCycle()

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

        // Play immediately
        playDeterrent()

        // Schedule next
        val intervalMs = currentSettings.intervalSeconds * 1000L
        deterrentRunnable = object : Runnable {
            override fun run() {
                if (isScreenOn && shouldPlayDeterrent()) {
                    playDeterrent()
                    handler.postDelayed(this, intervalMs)
                }
            }
        }
        handler.postDelayed(deterrentRunnable!!, intervalMs)
    }

    private fun playDeterrent() {
        player.play(
            currentSettings.soundSelection,
            currentSettings.vibrationPattern,
            currentSettings.deterrentMode,
            currentSettings.volumePercent
        )
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
