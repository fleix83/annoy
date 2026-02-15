package com.example.annoy

import android.app.Application
import com.example.annoy.util.SoundGenerator

class ScreenBrakeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SoundGenerator.ensureSoundsExist(this)
    }
}
