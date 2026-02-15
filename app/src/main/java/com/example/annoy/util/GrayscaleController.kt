package com.example.annoy.util

import android.content.Context
import android.provider.Settings

object GrayscaleController {

    private const val DALTONIZER_ENABLED = "accessibility_display_daltonizer_enabled"
    private const val DALTONIZER = "accessibility_display_daltonizer"

    fun isAvailable(context: Context): Boolean {
        return try {
            Settings.Secure.putInt(context.contentResolver, DALTONIZER_ENABLED, 0)
            Settings.Secure.putInt(context.contentResolver, DALTONIZER_ENABLED, 0)
            true
        } catch (e: SecurityException) {
            false
        }
    }

    fun isEnabled(context: Context): Boolean {
        return try {
            Settings.Secure.getInt(context.contentResolver, DALTONIZER_ENABLED, 0) == 1
        } catch (e: Exception) {
            false
        }
    }

    fun setEnabled(context: Context, enabled: Boolean): Boolean {
        return try {
            if (enabled) {
                Settings.Secure.putInt(context.contentResolver, DALTONIZER, 0) // 0 = grayscale
                Settings.Secure.putInt(context.contentResolver, DALTONIZER_ENABLED, 1)
            } else {
                Settings.Secure.putInt(context.contentResolver, DALTONIZER_ENABLED, 0)
                Settings.Secure.putInt(context.contentResolver, DALTONIZER, -1)
            }
            true
        } catch (e: SecurityException) {
            false
        }
    }
}
