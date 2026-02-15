package com.example.annoy.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.annoy.MainActivity
import com.example.annoy.R

object NotificationHelper {

    const val CHANNEL_ID = "screenbrake_service"
    const val NOTIFICATION_ID = 1

    const val ACTION_PAUSE = "com.example.annoy.ACTION_PAUSE"
    const val ACTION_STOP = "com.example.annoy.ACTION_STOP"

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "AnnoyMe Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Persistent notification while AnnoyMe is active"
            setShowBadge(false)
        }
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(channel)
    }

    fun buildNotification(context: Context, statusText: String, isPaused: Boolean): Notification {
        val contentIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pauseIntent = PendingIntent.getService(
            context, 1,
            Intent(context, DeterrentService::class.java).setAction(ACTION_PAUSE),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = PendingIntent.getService(
            context, 2,
            Intent(context, DeterrentService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("AnnoyMe")
            .setContentText(statusText)
            .setContentIntent(contentIntent)
            .setOngoing(true)
            .setSilent(true)
            .addAction(0, if (isPaused) "Resume" else "Pause", pauseIntent)
            .addAction(0, "Stop", stopIntent)

        return builder.build()
    }

    fun update(context: Context, statusText: String, isPaused: Boolean) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, buildNotification(context, statusText, isPaused))
    }
}
