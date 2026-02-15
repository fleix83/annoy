package com.example.annoy.service

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.annoy.util.PermissionUtils
import com.example.annoy.util.SoundGenerator

class DeterrentPlayer(private val context: Context) {

    private var soundPool: SoundPool? = null
    private val soundIds = mutableMapOf<String, Int>()
    private var vibrator: Vibrator
    private var currentStreamId: Int = 0

    init {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(attrs)
            .build()

        // Load base sounds and their escalation variants
        val sounds = listOf("mosquito", "hum", "beep", "ticking")
        for (name in sounds) {
            loadSound(name)
            for (level in 1..4) {
                loadSound(SoundGenerator.escalationName(name, level))
            }
        }

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    private fun loadSound(name: String) {
        val file = SoundGenerator.getSoundFile(context, name)
        if (file.exists()) {
            soundIds[name] = soundPool!!.load(file.absolutePath, 1)
        }
    }

    fun play(soundName: String, vibrationPattern: String, mode: String, volumePercent: Int) {
        val shouldSound = mode == "sound" || mode == "both"
        val shouldVibrate = mode == "vibration" || mode == "both"

        if (shouldSound && !PermissionUtils.isDndActive(context)) {
            playSound(soundName, volumePercent)
        }
        if (shouldVibrate) {
            playVibration(vibrationPattern)
        }
    }

    fun playEscalation(level: Int, soundSelection: String, vibrationPattern: String, mode: String, volumePercent: Int) {
        val shouldSound = mode == "sound" || mode == "both"
        val shouldVibrate = mode == "vibration" || mode == "both"

        if (shouldSound && !PermissionUtils.isDndActive(context)) {
            val soundName = SoundGenerator.escalationName(soundSelection, level)
            val loop = if (level == 4) -1 else 0
            val id = soundIds[soundName] ?: return
            val volume = volumePercent / 100f
            stopCurrentSound()
            currentStreamId = soundPool?.play(id, volume, volume, 1, loop, 1f) ?: 0
        }
        if (shouldVibrate) {
            val vibPattern = if (level >= 3) "long_buzz" else vibrationPattern
            playVibration(vibPattern)
        }
    }

    fun stopCurrentSound() {
        if (currentStreamId != 0) {
            soundPool?.stop(currentStreamId)
            currentStreamId = 0
        }
    }

    fun previewSound(soundName: String, volumePercent: Int) {
        playSound(soundName, volumePercent)
    }

    private fun playSound(soundName: String, volumePercent: Int) {
        val id = soundIds[soundName] ?: return
        val volume = volumePercent / 100f
        soundPool?.play(id, volume, volume, 1, 0, 1f)
    }

    private fun playVibration(pattern: String) {
        val effect = when (pattern) {
            "single_pulse" -> VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
            "double_tap" -> VibrationEffect.createWaveform(
                longArrayOf(0, 80, 50, 80), intArrayOf(0, 200, 0, 200), -1
            )
            "long_buzz" -> VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE)
            else -> VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
        }
        vibrator.vibrate(effect)
    }

    fun release() {
        stopCurrentSound()
        soundPool?.release()
        soundPool = null
    }
}
