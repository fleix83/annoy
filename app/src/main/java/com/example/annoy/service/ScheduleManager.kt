package com.example.annoy.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.annoy.data.UserSettings
import java.util.Calendar

class ScheduleManager(private val context: Context) {

    companion object {
        const val ACTION_SCHEDULE_START = "com.example.annoy.SCHEDULE_START"
        const val ACTION_SCHEDULE_END = "com.example.annoy.SCHEDULE_END"
        private const val REQUEST_START = 100
        private const val REQUEST_END = 101
    }

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun updateAlarms(settings: UserSettings) {
        cancelAlarms()
        if (settings.scheduleMode != "scheduled") return

        val now = Calendar.getInstance()

        val startCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, settings.scheduleStartHour)
            set(Calendar.MINUTE, settings.scheduleStartMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) add(Calendar.DAY_OF_YEAR, 1)
        }

        val endCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, settings.scheduleEndHour)
            set(Calendar.MINUTE, settings.scheduleEndMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) add(Calendar.DAY_OF_YEAR, 1)
        }

        setAlarm(startCal.timeInMillis, ACTION_SCHEDULE_START, REQUEST_START)
        setAlarm(endCal.timeInMillis, ACTION_SCHEDULE_END, REQUEST_END)
    }

    fun cancelAlarms() {
        cancelAlarm(ACTION_SCHEDULE_START, REQUEST_START)
        cancelAlarm(ACTION_SCHEDULE_END, REQUEST_END)
    }

    fun isInActiveWindow(settings: UserSettings): Boolean {
        if (settings.scheduleMode == "always") return true

        val now = Calendar.getInstance()
        val dayName = when (now.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "MON"
            Calendar.TUESDAY -> "TUE"
            Calendar.WEDNESDAY -> "WED"
            Calendar.THURSDAY -> "THU"
            Calendar.FRIDAY -> "FRI"
            Calendar.SATURDAY -> "SAT"
            Calendar.SUNDAY -> "SUN"
            else -> ""
        }
        if (dayName !in settings.scheduleActiveDays) return false

        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        val startMinutes = settings.scheduleStartHour * 60 + settings.scheduleStartMinute
        val endMinutes = settings.scheduleEndHour * 60 + settings.scheduleEndMinute

        return if (startMinutes <= endMinutes) {
            // Normal: e.g. 08:00-22:00
            currentMinutes in startMinutes until endMinutes
        } else {
            // Overnight: e.g. 22:00-06:00
            currentMinutes >= startMinutes || currentMinutes < endMinutes
        }
    }

    private fun setAlarm(timeMillis: Long, action: String, requestCode: Int) {
        val intent = Intent(context, ScheduleReceiver::class.java).setAction(action)
        val pi = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMillis, pi)
        } catch (e: SecurityException) {
            // Fall back to inexact alarm if exact alarm permission not granted
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMillis, pi)
        }
    }

    private fun cancelAlarm(action: String, requestCode: Int) {
        val intent = Intent(context, ScheduleReceiver::class.java).setAction(action)
        val pi = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pi)
    }
}
