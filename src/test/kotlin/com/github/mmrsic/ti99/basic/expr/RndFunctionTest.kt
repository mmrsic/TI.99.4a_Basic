package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.test.assertTrue

class RndFunctionTest {

    @Test
    fun testBordersZeroAndTen() {
        for (turn in 1..1000) {
            val randomValue = 10 * RndFunction().calculate().toDouble()
            assertTrue(randomValue >= 0, "Random value ($randomValue) must not be less than zero")
            assertTrue(randomValue < 10, "Random value ($randomValue) must be less than ten")
        }
    }
}