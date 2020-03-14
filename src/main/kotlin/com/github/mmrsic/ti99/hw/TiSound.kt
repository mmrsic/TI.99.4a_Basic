package com.github.mmrsic.ti99.hw

import com.github.mmrsic.ti99.basic.expr.NumericConstant
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import javax.sound.sampled.*
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sign
import kotlin.math.sin

/**
 * Interface for producing sound on a TI 99/4(a) computer.
 */
interface TiSound {
    /**
     * Play up to three tones and at most one noise for a given duration.
     * @param duration Number of milliseconds to hold play the tones - must be >= -4250 and <= 4250 and not zero
     * @param freq1 Frequency (Hertz) of the first tone - must be >= 110 Hz and <= 44733
     * @param volume1 Volume from 0 (loudest) to 30 (quietest)
     */
    fun play(
        duration: NumericConstant,
        freq1: NumericConstant,
        volume1: NumericConstant,
        freq2: NumericConstant? = null,
        vol2: NumericConstant? = null,
        freq3: NumericConstant? = null,
        vol3: NumericConstant? = null
    )
}

/** Dummy implementation of [TiSound] not actually playing any sound. */
class TiSoundDummy : TiSound {
    override fun play(
        duration: NumericConstant,
        freq1: NumericConstant,
        volume1: NumericConstant,
        freq2: NumericConstant?,
        vol2: NumericConstant?,
        freq3: NumericConstant?,
        vol3: NumericConstant?
    ) {
        println("Playing sound: $duration, $freq1, $volume1, $freq2, $vol2, $freq3, $vol3")
    }
}

/** A [TiSound] implementation using the javax.sound API. */
class TiSoundJava : TiSound {

    // TODO: Introduce three tone generators and a noise generator for simultaneous play

    override fun play(
        duration: NumericConstant,
        freq1: NumericConstant,
        volume1: NumericConstant,
        freq2: NumericConstant?,
        vol2: NumericConstant?,
        freq3: NumericConstant?,
        vol3: NumericConstant?
    ) {
        val seconds = duration.toNative() / 1000
        val freq = freq1.toNative()
        val volume = volume1.toNative()

        val amplitude = 16000 - volume * 400
        val sampleRate = 16000
        val bytesPerSample = 2
        val channels = 1
        val signed = true
        val bigEndian = true
        val audioFormat = AudioFormat(sampleRate.toFloat(), bytesPerSample * 8, channels, signed, bigEndian)
        val audioData = ByteArray((sampleRate * seconds * bytesPerSample).roundToInt())
        val numSampleFrames = audioData.size / audioFormat.frameSize
        val audioInputStream = AudioInputStream(ByteArrayInputStream(audioData), audioFormat, numSampleFrames.toLong())
        val dateLineInfo = DataLine.Info(SourceDataLine::class.java, audioFormat)
        val sourceDataLine = AudioSystem.getLine(dateLineInfo) as SourceDataLine

        val numSamples: Int = audioData.size / bytesPerSample
        val audioDataBuffer = ByteBuffer.wrap(audioData).asShortBuffer()
        val sampleRateDivisor = sampleRate.toDouble()
        for (cnt in 0 until numSamples) {
            val time: Double = cnt / sampleRateDivisor
            val sinValue = sin(2 * PI * freq * time)
            audioDataBuffer.put((amplitude * sign(sinValue)).toShort())
        }

        val playBuffer = ByteArray(16384)
        sourceDataLine.use { line ->
            line.open(audioFormat)
            line.start()
            var cnt = audioInputStream.read(playBuffer, 0, playBuffer.size)
            while (cnt > 0) {
                line.write(playBuffer, 0, cnt)
                cnt = audioInputStream.read(playBuffer, 0, playBuffer.size)
            }
            line.drain()
            line.stop()
        }
    }
}