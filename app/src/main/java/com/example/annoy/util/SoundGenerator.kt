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
        "mosquito" to SoundSpec(frequency = 5000.0, durationMs = 500),
        "hum" to SoundSpec(frequency = 250.0, durationMs = 800),
        "beep" to SoundSpec(frequency = 1000.0, durationMs = 300),
        "ticking" to SoundSpec(frequency = 2000.0, durationMs = 1000)
    )

    fun escalationName(sound: String, level: Int) = "${sound}_esc$level"

    fun ensureSoundsExist(context: Context) {
        val cacheDir = File(context.cacheDir, "sounds")
        if (!cacheDir.exists()) cacheDir.mkdirs()

        // Base sounds
        for ((name, spec) in SOUNDS) {
            val file = File(cacheDir, "$name.wav")
            if (!file.exists()) {
                val pcm = if (name == "ticking") generateTicking(spec.durationMs) else generateTone(spec.frequency, spec.durationMs)
                writeWav(file, pcm)
            }
        }

        // Escalation variants for each sound at its own frequency
        for ((name, spec) in SOUNDS) {
            val freq = spec.frequency
            generateIfMissing(cacheDir, escalationName(name, 1)) { generateDoublePattern(freq) }
            generateIfMissing(cacheDir, escalationName(name, 2)) { generateRisingPattern(freq) }
            generateIfMissing(cacheDir, escalationName(name, 3)) { generateRapidPattern(freq) }
            generateIfMissing(cacheDir, escalationName(name, 4)) { generateContinuousPattern(freq) }
        }
    }

    fun getSoundFile(context: Context, name: String): File {
        return File(File(context.cacheDir, "sounds"), "$name.wav")
    }

    private fun generateIfMissing(cacheDir: File, name: String, generator: () -> ShortArray) {
        val file = File(cacheDir, "$name.wav")
        if (!file.exists()) {
            writeWav(file, generator())
        }
    }

    // Level 1: double burst (two short tones with gap) ~900ms
    private fun generateDoublePattern(freq: Double): ShortArray {
        val burstSamples = SAMPLE_RATE * 300 / 1000
        val gapSamples = SAMPLE_RATE * 300 / 1000
        val total = burstSamples * 2 + gapSamples
        val samples = ShortArray(total)
        val angularFreq = 2.0 * PI * freq / SAMPLE_RATE
        val fadeSamples = SAMPLE_RATE * 10 / 1000

        for (i in 0 until burstSamples) {
            var amp = sin(angularFreq * i)
            amp *= fadeEnvelope(i, burstSamples, fadeSamples)
            samples[i] = (amp * Short.MAX_VALUE * 0.8).toInt().toShort()
        }
        val offset2 = burstSamples + gapSamples
        for (i in 0 until burstSamples) {
            var amp = sin(angularFreq * (offset2 + i))
            amp *= fadeEnvelope(i, burstSamples, fadeSamples)
            samples[offset2 + i] = (amp * Short.MAX_VALUE * 0.8).toInt().toShort()
        }
        return samples
    }

    // Level 2: short + long burst (300ms + gap + 1500ms) ~2100ms
    private fun generateRisingPattern(freq: Double): ShortArray {
        val shortBurst = SAMPLE_RATE * 300 / 1000
        val gap = SAMPLE_RATE * 300 / 1000
        val longBurst = SAMPLE_RATE * 1500 / 1000
        val total = shortBurst + gap + longBurst
        val samples = ShortArray(total)
        val angularFreq = 2.0 * PI * freq / SAMPLE_RATE
        val fadeSamples = SAMPLE_RATE * 10 / 1000

        for (i in 0 until shortBurst) {
            var amp = sin(angularFreq * i)
            amp *= fadeEnvelope(i, shortBurst, fadeSamples)
            samples[i] = (amp * Short.MAX_VALUE * 0.8).toInt().toShort()
        }
        val offset = shortBurst + gap
        for (i in 0 until longBurst) {
            var amp = sin(angularFreq * (offset + i))
            amp *= fadeEnvelope(i, longBurst, fadeSamples)
            samples[offset + i] = (amp * Short.MAX_VALUE * 0.8).toInt().toShort()
        }
        return samples
    }

    // Level 3: rapid bursts (100ms on, 100ms off) for 5 seconds
    private fun generateRapidPattern(freq: Double): ShortArray {
        val numSamples = SAMPLE_RATE * 5000 / 1000
        val samples = ShortArray(numSamples)
        val angularFreq = 2.0 * PI * freq / SAMPLE_RATE
        val onSamples = SAMPLE_RATE * 100 / 1000
        val cycleSamples = SAMPLE_RATE * 200 / 1000
        val fadeSamples = SAMPLE_RATE * 5 / 1000

        for (i in 0 until numSamples) {
            val posInCycle = i % cycleSamples
            if (posInCycle < onSamples) {
                var amp = sin(angularFreq * i)
                amp *= fadeEnvelope(posInCycle, onSamples, fadeSamples)
                samples[i] = (amp * Short.MAX_VALUE * 0.8).toInt().toShort()
            }
        }
        return samples
    }

    // Level 4: continuous tone for 5 seconds
    private fun generateContinuousPattern(freq: Double): ShortArray {
        return generateTone(freq, 5000)
    }

    private fun generateTone(frequency: Double, durationMs: Int): ShortArray {
        val numSamples = SAMPLE_RATE * durationMs / 1000
        val samples = ShortArray(numSamples)
        val angularFreq = 2.0 * PI * frequency / SAMPLE_RATE
        val fadeSamples = SAMPLE_RATE * 10 / 1000
        for (i in 0 until numSamples) {
            var amplitude = sin(angularFreq * i)
            amplitude *= fadeEnvelope(i, numSamples, fadeSamples)
            samples[i] = (amplitude * Short.MAX_VALUE * 0.8).toInt().toShort()
        }
        return samples
    }

    private fun generateTicking(durationMs: Int): ShortArray {
        val numSamples = SAMPLE_RATE * durationMs / 1000
        val samples = ShortArray(numSamples)
        val tickIntervalSamples = SAMPLE_RATE / 4
        val tickDurationSamples = SAMPLE_RATE * 5 / 1000
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

    private fun fadeEnvelope(position: Int, totalSamples: Int, fadeSamples: Int): Double {
        return when {
            position < fadeSamples -> position.toDouble() / fadeSamples
            position > totalSamples - fadeSamples -> (totalSamples - position).toDouble() / fadeSamples
            else -> 1.0
        }
    }

    private fun writeWav(file: File, pcm: ShortArray) {
        val dataSize = pcm.size * 2
        val buffer = ByteBuffer.allocate(44 + dataSize).order(ByteOrder.LITTLE_ENDIAN)
        buffer.put("RIFF".toByteArray())
        buffer.putInt(36 + dataSize)
        buffer.put("WAVE".toByteArray())
        buffer.put("fmt ".toByteArray())
        buffer.putInt(16)
        buffer.putShort(1)
        buffer.putShort(1)
        buffer.putInt(SAMPLE_RATE)
        buffer.putInt(SAMPLE_RATE * 2)
        buffer.putShort(2)
        buffer.putShort(16)
        buffer.put("data".toByteArray())
        buffer.putInt(dataSize)
        for (sample in pcm) {
            buffer.putShort(sample)
        }
        FileOutputStream(file).use { it.write(buffer.array()) }
    }

    private data class SoundSpec(val frequency: Double, val durationMs: Int)
}
