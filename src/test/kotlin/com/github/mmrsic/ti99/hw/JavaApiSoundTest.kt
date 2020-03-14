package com.github.mmrsic.ti99.hw

import com.github.mmrsic.ti99.basic.expr.NumericConstant
import org.junit.Test

/**
 * Test cases for [TiSoundJava].
 */
class JavaApiSoundTest {

    @Test
    fun testSimpleShortTone() {
        val duration = NumericConstant(25)
        val frequency = NumericConstant(220)
        val volume = NumericConstant.ZERO
        TiSoundJava().play(duration, frequency, volume) // Should produce a very short sound of 220 Hz
    }

}