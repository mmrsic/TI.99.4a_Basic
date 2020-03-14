package com.github.mmrsic.ti99.hw

import com.github.mmrsic.ti99.basic.expr.NumericConstant

/**
 * Interface for producing sound on a TI 99/4(a) computer.
 */
interface TiSound {
    /** Play up to three tones and at most one noise for a given duration. */
    fun play(
        duration: NumericConstant,
        freq1: NumericConstant,
        volume1: NumericConstant,
        freq2: NumericConstant?,
        vol2: NumericConstant?,
        freq3: NumericConstant?,
        vol3: NumericConstant?
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