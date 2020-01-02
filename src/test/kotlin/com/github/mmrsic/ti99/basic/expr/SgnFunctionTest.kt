package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.test.assertEquals

class SgnFunctionTest {

    @Test
    fun testFivePointEight() {
        val argument = NumericConstant(5.8)
        val func = SgnFunction(argument)
        assertEquals(1, func.calculate(), "SGN($argument)")
    }

    @Test
    fun testMinusFive() {
        val argument = NumericConstant(-5)
        val func = SgnFunction(argument)
        assertEquals(-1, func.calculate(), "SGN($argument)")
    }

    @Test
    fun testZero() {
        val argument = NumericConstant(0)
        val func = SgnFunction(argument)
        assertEquals(0, func.calculate(), "SGN($argument)")
    }

}