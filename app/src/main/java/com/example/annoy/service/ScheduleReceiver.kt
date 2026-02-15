package com.example.annoy.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // When a schedule alarm fires, just poke the service to re-evaluate.
        // The service already collects settings and checks isInActiveWindow.
        val serviceIntent = Intent(context, DeterrentService::class.java).apply {
            action = intent.action
        }
        try {
            context.startForegroundService(serviceIntent)
        } catch (e: Exception) {
            // Service may already be running; ignore
        }
    }
}
