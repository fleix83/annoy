package com.example.annoy.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.annoy.ui.components.AboutSection
import com.example.annoy.ui.components.DeterrentConfigCard
import com.example.annoy.ui.components.GrayscaleCard
import com.example.annoy.ui.components.MasterToggleCard
import com.example.annoy.ui.components.PauseButton
import com.example.annoy.ui.components.ScheduleCard
import com.example.annoy.ui.components.StatusSection

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val settings by viewModel.settings.collectAsState()
    val pauseCountdown by viewModel.pauseCountdown.collectAsState()

    val nextActiveTime = "${settings.scheduleStartHour.toString().padStart(2, '0')}:" +
            settings.scheduleStartMinute.toString().padStart(2, '0')

    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                MasterToggleCard(
                    enabled = settings.masterEnabled,
                    onToggle = { viewModel.setMasterEnabled(it) },
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                StatusSection(
                    isEnabled = settings.masterEnabled,
                    isPaused = settings.isPaused,
                    pauseCountdown = pauseCountdown,
                    isScheduled = settings.scheduleMode == "scheduled",
                    isInWindow = true, // Simplified: service handles actual check
                    nextActiveTime = nextActiveTime
                )
            }

            item {
                DeterrentConfigCard(
                    deterrentMode = settings.deterrentMode,
                    soundSelection = settings.soundSelection,
                    vibrationPattern = settings.vibrationPattern,
                    intervalSeconds = settings.intervalSeconds,
                    volumePercent = settings.volumePercent,
                    gracePeriodSeconds = settings.gracePeriodSeconds,
                    onModeChange = viewModel::setDeterrentMode,
                    onSoundChange = viewModel::setSoundSelection,
                    onVibrationChange = viewModel::setVibrationPattern,
                    onIntervalChange = viewModel::setIntervalSeconds,
                    onVolumeChange = viewModel::setVolumePercent,
                    onGracePeriodChange = viewModel::setGracePeriodSeconds
                )
            }

            item {
                GrayscaleCard(
                    enabled = settings.grayscaleEnabled,
                    available = viewModel.grayscaleAvailable,
                    onToggle = viewModel::toggleGrayscale
                )
            }

            item {
                ScheduleCard(
                    scheduleMode = settings.scheduleMode,
                    startHour = settings.scheduleStartHour,
                    startMinute = settings.scheduleStartMinute,
                    endHour = settings.scheduleEndHour,
                    endMinute = settings.scheduleEndMinute,
                    activeDays = settings.scheduleActiveDays,
                    onModeChange = viewModel::setScheduleMode,
                    onStartTimeChange = viewModel::setScheduleStartTime,
                    onEndTimeChange = viewModel::setScheduleEndTime,
                    onDaysChange = viewModel::setScheduleActiveDays
                )
            }

            if (settings.masterEnabled) {
                item {
                    PauseButton(
                        isPaused = settings.isPaused,
                        pauseCountdown = pauseCountdown,
                        defaultMinutes = settings.pauseDefaultMinutes,
                        onPause = viewModel::pause,
                        onResume = viewModel::resumeFromPause,
                        onDefaultChange = viewModel::setPauseDefaultMinutes
                    )
                }
            }

            item {
                AboutSection(modifier = Modifier.padding(bottom = 16.dp))
            }
        }
    }
}
