package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.test.assertEquals

class SinFunctionTest {

    @Test
    fun testPointFiveSomething() {
        val argument = NumericConstant(.5235987755982)
        val func = SinFunction(argument)
        assertEquals(.5, func.calculateToConstant().value(), "SIN($argument)")
    }

}