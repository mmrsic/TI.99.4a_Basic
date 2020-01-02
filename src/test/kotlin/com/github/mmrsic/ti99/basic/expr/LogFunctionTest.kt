package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.test.assertEquals

class LogFunctionTest {

    @Test
    fun testThreePointFour() {
        val argument = 3.4
        val func = LogFunction(NumericConstant(argument))
        assertEquals(1.2237754316, func.calculateToConstant().value(), "LOG($argument)")
    }

    @Test
    fun testInverseFunction() {
        val argument = 7.2
        val exp = ExpFunction(NumericConstant(argument)).calculate()
        val func = LogFunction(NumericConstant(exp))
        assertEquals(argument, func.calculateToConstant().value())
    }

}