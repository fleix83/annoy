package com.example.annoy.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.annoy.data.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import com.example.annoy.data.PreferenceKeys

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val enabled = runBlocking {
            context.dataStore.data.map { it[PreferenceKeys.MASTER_ENABLED] ?: false }.first()
        }

        if (enabled) {
            val serviceIntent = Intent(context, DeterrentService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}
