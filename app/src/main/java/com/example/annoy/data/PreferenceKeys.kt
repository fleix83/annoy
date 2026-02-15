package com.example.annoy.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

object PreferenceKeys {
    val MASTER_ENABLED = booleanPreferencesKey("master_enabled")
    val DETERRENT_MODE = stringPreferencesKey("deterrent_mode")
    val SOUND_SELECTION = stringPreferencesKey("sound_selection")
    val VIBRATION_PATTERN = stringPreferencesKey("vibration_pattern")
    val INTERVAL_SECONDS = intPreferencesKey("interval_seconds")
    val VOLUME_PERCENT = intPreferencesKey("volume_percent")
    val GRACE_PERIOD_SECONDS = intPreferencesKey("grace_period_seconds")
    val GRAYSCALE_ENABLED = booleanPreferencesKey("grayscale_enabled")
    val SCHEDULE_MODE = stringPreferencesKey("schedule_mode")
    val SCHEDULE_START_HOUR = intPreferencesKey("schedule_start_hour")
    val SCHEDULE_START_MINUTE = intPreferencesKey("schedule_start_minute")
    val SCHEDULE_END_HOUR = intPreferencesKey("schedule_end_hour")
    val SCHEDULE_END_MINUTE = intPreferencesKey("schedule_end_minute")
    val SCHEDULE_ACTIVE_DAYS = stringSetPreferencesKey("schedule_active_days")
    val PAUSE_END_TIMESTAMP = longPreferencesKey("pause_end_timestamp")
    val PAUSE_DEFAULT_MINUTES = intPreferencesKey("pause_default_minutes")
}
