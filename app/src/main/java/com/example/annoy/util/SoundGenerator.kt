package com.example.annoy.util

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.sin

object SoundGenerator {

    private const val SAMPLE_RATE = 44100

    private val SOUNDS = mapOf(
        "mosquito" to SoundSpec(frequency = 14000.0, durationMs = 500),
        "hum" to SoundSpec(frequency = 100.0, durationMs = 800),
        "beep" to SoundSpec(frequency = 1000.0, durationMs = 300),
        "ticking" to SoundSpec(frequency = 0.0, durationMs = 1000) // special case
    )

    fun ensureSoundsExist(context: Context) {
        val cacheDir = File(context.cacheDir, "sounds")
        if (!cacheDir.exists()) cacheDir.mkdirs()
        for ((name, spec) in SOUNDS) {
            val file = File(cacheDir, "$name.wav")
            if (!file.exists()) {
                val pcm = if (name == "ticking") generateTicking(spec.durationMs) else generateTone(spec.frequency, spec.durationMs)
                writeWav(file, pcm)
            }
        }
    }

    fun getSoundFile(context: Context, name: String): File {
        return File(File(context.cacheDir, "sounds"), "$name.wav")
    }

    private fun generateTone(frequency: Double, durationMs: Int): ShortArray {
        val numSamples = SAMPLE_RATE * durationMs / 1000
        val samples = ShortArray(numSamples)
        val angularFreq = 2.0 * PI * frequency / SAMPLE_RATE
        // Apply fade in/out to avoid clicks (10ms)
        val fadeSamples = SAMPLE_RATE * 10 / 1000
        for (i in 0 until numSamples) {
            var amplitude = sin(angularFreq * i)
            // Fade envelope
            if (i < fadeSamples) {
                amplitude *= i.toDouble() / fadeSamples
            } else if (i > numSamples - fadeSamples) {
                amplitude *= (numSamples - i).toDouble() / fadeSamples
            }
            samples[i] = (amplitude * Short.MAX_VALUE * 0.8).toInt().toShort()
        }
        return samples
    }

    private fun generateTicking(durationMs: Int): ShortArray {
        val numSamples = SAMPLE_RATE * durationMs / 1000
        val samples = ShortArray(numSamples)
        val tickIntervalSamples = SAMPLE_RATE / 4 // 4 ticks per second
        val tickDurationSamples = SAMPLE_RATE * 5 / 1000 // 5ms per tick
        for (i in 0 until numSamples) {
            val posInInterval = i % tickIntervalSamples
            if (posInInterval < tickDurationSamples) {
                val decay = 1.0 - posInInterval.toDouble() / tickDurationSamples
                val tone = sin(2.0 * PI * 2000.0 * i / SAMPLE_RATE)
                samples[i] = (tone * decay * Short.MAX_VALUE * 0.7).toInt().toShort()
            }
        }
        return samples
    }

    private fun writeWav(file: File, pcm: ShortArray) {
        val dataSize = pcm.size * 2
        val buffer = ByteBuffer.allocate(44 + dataSize).order(ByteOrder.LITTLE_ENDIAN)
        // RIFF header
        buffer.put("RIFF".toByteArray())
        buffer.putInt(36 + dataSize)
        buffer.put("WAVE".toByteArray())
        // fmt chunk
        buffer.put("fmt ".toByteArray())
        buffer.putInt(16) // chunk size
        buffer.putShort(1) // PCM format
        buffer.putShort(1) // mono
        buffer.putInt(SAMPLE_RATE)
        buffer.putInt(SAMPLE_RATE * 2) // byte rate
        buffer.putShort(2) // block align
        buffer.putShort(16) // bits per sample
        // data chunk
        buffer.put("data".toByteArray())
        buffer.putInt(dataSize)
        for (sample in pcm) {
            buffer.putShort(sample)
        }
        FileOutputStream(file).use { it.write(buffer.array()) }
    }

    private data class SoundSpec(val frequency: Double, val durationMs: Int)
}
