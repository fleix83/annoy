package com.example.annoy.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "screenbrake_preferences")

class SettingsRepository(private val context: Context) {

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            masterEnabled = prefs[PreferenceKeys.MASTER_ENABLED] ?: false,
            deterrentMode = prefs[PreferenceKeys.DETERRENT_MODE] ?: "both",
            soundSelection = prefs[PreferenceKeys.SOUND_SELECTION] ?: "mosquito",
            vibrationPattern = prefs[PreferenceKeys.VIBRATION_PATTERN] ?: "double_tap",
            intervalSeconds = prefs[PreferenceKeys.INTERVAL_SECONDS] ?: 15,
            volumePercent = prefs[PreferenceKeys.VOLUME_PERCENT] ?: 25,
            gracePeriodSeconds = prefs[PreferenceKeys.GRACE_PERIOD_SECONDS] ?: 5,
            grayscaleEnabled = prefs[PreferenceKeys.GRAYSCALE_ENABLED] ?: false,
            scheduleMode = prefs[PreferenceKeys.SCHEDULE_MODE] ?: "always",
            scheduleStartHour = prefs[PreferenceKeys.SCHEDULE_START_HOUR] ?: 8,
            scheduleStartMinute = prefs[PreferenceKeys.SCHEDULE_START_MINUTE] ?: 0,
            scheduleEndHour = prefs[PreferenceKeys.SCHEDULE_END_HOUR] ?: 22,
            scheduleEndMinute = prefs[PreferenceKeys.SCHEDULE_END_MINUTE] ?: 0,
            scheduleActiveDays = prefs[PreferenceKeys.SCHEDULE_ACTIVE_DAYS]
                ?: setOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"),
            pauseEndTimestamp = prefs[PreferenceKeys.PAUSE_END_TIMESTAMP] ?: 0L,
            pauseDefaultMinutes = prefs[PreferenceKeys.PAUSE_DEFAULT_MINUTES] ?: 15
        )
    }

    suspend fun setMasterEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferenceKeys.MASTER_ENABLED] = enabled }
    }

    suspend fun setDeterrentMode(mode: String) {
        context.dataStore.edit { it[PreferenceKeys.DETERRENT_MODE] = mode }
    }

    suspend fun setSoundSelection(sound: String) {
        context.dataStore.edit { it[PreferenceKeys.SOUND_SELECTION] = sound }
    }

    suspend fun setVibrationPattern(pattern: String) {
        context.dataStore.edit { it[PreferenceKeys.VIBRATION_PATTERN] = pattern }
    }

    suspend fun setIntervalSeconds(seconds: Int) {
        context.dataStore.edit { it[PreferenceKeys.INTERVAL_SECONDS] = seconds }
    }

    suspend fun setVolumePercent(percent: Int) {
        context.dataStore.edit { it[PreferenceKeys.VOLUME_PERCENT] = percent.coerceIn(10, 50) }
    }

    suspend fun setGracePeriodSeconds(seconds: Int) {
        context.dataStore.edit { it[PreferenceKeys.GRACE_PERIOD_SECONDS] = seconds }
    }

    suspend fun setGrayscaleEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferenceKeys.GRAYSCALE_ENABLED] = enabled }
    }

    suspend fun setScheduleMode(mode: String) {
        context.dataStore.edit { it[PreferenceKeys.SCHEDULE_MODE] = mode }
    }

    suspend fun setScheduleStartTime(hour: Int, minute: Int) {
        context.dataStore.edit {
            it[PreferenceKeys.SCHEDULE_START_HOUR] = hour
            it[PreferenceKeys.SCHEDULE_START_MINUTE] = minute
        }
    }

    suspend fun setScheduleEndTime(hour: Int, minute: Int) {
        context.dataStore.edit {
            it[PreferenceKeys.SCHEDULE_END_HOUR] = hour
            it[PreferenceKeys.SCHEDULE_END_MINUTE] = minute
        }
    }

    suspend fun setScheduleActiveDays(days: Set<String>) {
        context.dataStore.edit { it[PreferenceKeys.SCHEDULE_ACTIVE_DAYS] = days }
    }

    suspend fun setPauseEndTimestamp(timestamp: Long) {
        context.dataStore.edit { it[PreferenceKeys.PAUSE_END_TIMESTAMP] = timestamp }
    }

    suspend fun setPauseDefaultMinutes(minutes: Int) {
        context.dataStore.edit { it[PreferenceKeys.PAUSE_DEFAULT_MINUTES] = minutes }
    }
}
