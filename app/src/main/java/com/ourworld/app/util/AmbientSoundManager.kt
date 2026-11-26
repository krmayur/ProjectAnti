package com.ourworld.app.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.*
import kotlin.math.PI
import kotlin.math.sin

/**
 * Generates a soft, warm pastel ambient chord pad (C-major 9th gentle sine harmonics)
 * using AudioTrack so the app has relaxing background ambiance with zero external audio assets.
 */
class AmbientSoundManager {
    private var job: Job? = null
    private var audioTrack: AudioTrack? = null
    private val sampleRate = 22050

    var isPlaying: Boolean = false
        private set

    fun toggle(): Boolean {
        if (isPlaying) {
            stop()
        } else {
            start()
        }
        return isPlaying
    }

    fun start() {
        if (isPlaying) return
        isPlaying = true

        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(4096)

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack = track
        track.play()

        job = CoroutineScope(Dispatchers.Default).launch {
            val buffer = ShortArray(2048)
            // Warm cozy chord frequencies: C4, E4, G4, B4, D5
            val freqs = doubleArrayOf(261.63, 329.63, 392.00, 493.88, 587.33)
            var sampleIndex = 0L

            while (isActive && isPlaying) {
                for (i in buffer.indices) {
                    val t = sampleIndex.toDouble() / sampleRate
                    // Slow breathing LFO so volume gently swells
                    val swell = 0.55 + 0.45 * sin(2.0 * PI * 0.12 * t)
                    var sum = 0.0
                    for (fIdx in freqs.indices) {
                        val voiceSwell = 0.6 + 0.4 * sin(2.0 * PI * (0.07 + fIdx * 0.03) * t)
                        sum += sin(2.0 * PI * freqs[fIdx] * t) * voiceSwell
                    }
                    val sample = (sum / freqs.size) * swell * 1800.0
                    buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                    sampleIndex++
                }
                track.write(buffer, 0, buffer.size)
            }
        }
    }

    fun stop() {
        isPlaying = false
        job?.cancel()
        job = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }
}
