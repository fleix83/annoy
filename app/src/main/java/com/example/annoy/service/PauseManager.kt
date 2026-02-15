package com.example.annoy.service

import com.example.annoy.data.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PauseManager(
    private val repository: SettingsRepository,
    private val scope: CoroutineScope
) {
    fun pause(minutes: Int) {
        val endTimestamp = System.currentTimeMillis() + minutes * 60_000L
        scope.launch { repository.setPauseEndTimestamp(endTimestamp) }
    }

    fun resume() {
        scope.launch { repository.setPauseEndTimestamp(0L) }
    }

    fun isPaused(pauseEndTimestamp: Long): Boolean {
        return pauseEndTimestamp > System.currentTimeMillis()
    }
}
