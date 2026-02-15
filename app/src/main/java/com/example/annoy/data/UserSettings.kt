package com.example.annoy.data

data class UserSettings(
    val masterEnabled: Boolean = false,
    val deterrentMode: String = "both",
    val soundSelection: String = "mosquito",
    val vibrationPattern: String = "double_tap",
    val intervalSeconds: Int = 15,
    val volumePercent: Int = 25,
    val gracePeriodSeconds: Int = 5,
    val grayscaleEnabled: Boolean = false,
    val scheduleMode: String = "always",
    val scheduleStartHour: Int = 8,
    val scheduleStartMinute: Int = 0,
    val scheduleEndHour: Int = 22,
    val scheduleEndMinute: Int = 0,
    val scheduleActiveDays: Set<String> = setOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"),
    val pauseEndTimestamp: Long = 0L,
    val pauseDefaultMinutes: Int = 15
) {
    val isPaused: Boolean get() = pauseEndTimestamp > System.currentTimeMillis()
    val pauseRemainingMillis: Long get() = if (isPaused) pauseEndTimestamp - System.currentTimeMillis() else 0L
}
