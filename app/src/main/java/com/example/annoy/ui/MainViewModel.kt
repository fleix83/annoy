package com.example.annoy.ui

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.annoy.data.SettingsRepository
import com.example.annoy.data.UserSettings
import com.example.annoy.service.DeterrentService
import com.example.annoy.util.GrayscaleController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application)

    val settings: StateFlow<UserSettings> = repository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    private val _pauseCountdown = MutableStateFlow("")
    val pauseCountdown: StateFlow<String> = _pauseCountdown.asStateFlow()

    private val _grayscaleAvailable = MutableStateFlow(GrayscaleController.isAvailable(application))
    val grayscaleAvailable: StateFlow<Boolean> = _grayscaleAvailable.asStateFlow()

    fun recheckGrayscale() {
        _grayscaleAvailable.value = GrayscaleController.isAvailable(getApplication())
    }

    init {
        // Tick the pause countdown every second
        viewModelScope.launch {
            while (true) {
                val s = settings.value
                if (s.pauseEndTimestamp > 0L && s.pauseEndTimestamp <= System.currentTimeMillis()) {
                    // Pause just expired — clear it to notify the service
                    repository.setPauseEndTimestamp(0L)
                    _pauseCountdown.value = ""
                } else if (s.isPaused) {
                    val remaining = s.pauseRemainingMillis
                    val mins = remaining / 60_000
                    val secs = (remaining % 60_000) / 1000
                    _pauseCountdown.value = "${mins}m ${secs}s"
                } else {
                    _pauseCountdown.value = ""
                }
                delay(1000)
            }
        }
    }

    fun setMasterEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setMasterEnabled(enabled)
            val context = getApplication<Application>()
            if (enabled) {
                context.startForegroundService(Intent(context, DeterrentService::class.java))
            } else {
                context.stopService(Intent(context, DeterrentService::class.java))
            }
        }
    }

    fun setDeterrentMode(mode: String) {
        viewModelScope.launch { repository.setDeterrentMode(mode) }
    }

    fun setSoundSelection(sound: String) {
        viewModelScope.launch { repository.setSoundSelection(sound) }
    }

    fun setVibrationPattern(pattern: String) {
        viewModelScope.launch { repository.setVibrationPattern(pattern) }
    }

    fun setIntervalSeconds(seconds: Int) {
        viewModelScope.launch { repository.setIntervalSeconds(seconds) }
    }

    fun setVolumePercent(percent: Int) {
        viewModelScope.launch { repository.setVolumePercent(percent) }
    }

    fun setGracePeriodSeconds(seconds: Int) {
        viewModelScope.launch { repository.setGracePeriodSeconds(seconds) }
    }

    fun toggleGrayscale() {
        viewModelScope.launch {
            val current = settings.value.grayscaleEnabled
            val newState = !current
            val success = GrayscaleController.setEnabled(getApplication(), newState)
            if (success) {
                repository.setGrayscaleEnabled(newState)
            }
        }
    }

    fun setScheduleMode(mode: String) {
        viewModelScope.launch { repository.setScheduleMode(mode) }
    }

    fun setScheduleStartTime(hour: Int, minute: Int) {
        viewModelScope.launch { repository.setScheduleStartTime(hour, minute) }
    }

    fun setScheduleEndTime(hour: Int, minute: Int) {
        viewModelScope.launch { repository.setScheduleEndTime(hour, minute) }
    }

    fun setScheduleActiveDays(days: Set<String>) {
        viewModelScope.launch { repository.setScheduleActiveDays(days) }
    }

    fun pause(minutes: Int) {
        viewModelScope.launch {
            val endTimestamp = System.currentTimeMillis() + minutes * 60_000L
            repository.setPauseEndTimestamp(endTimestamp)
        }
    }

    fun resumeFromPause() {
        viewModelScope.launch {
            repository.setPauseEndTimestamp(0L)
        }
    }

    fun setPauseDefaultMinutes(minutes: Int) {
        viewModelScope.launch { repository.setPauseDefaultMinutes(minutes) }
    }
}
