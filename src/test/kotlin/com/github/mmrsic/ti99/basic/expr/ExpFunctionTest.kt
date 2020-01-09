package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.test.assertEquals

class ExpFunctionTest {

    @Test
    fun testExpSeven() {
        val result = ExpFunction(NumericConstant(7)).value()
        assertEquals(" 1096.633158 ", result.displayValue(), "EXP(7)")
        assertEquals(1096.6331584285, result.toNative(), "EXP(7)")
    }

    @Test
    fun testExpFourSomething() {
        val result = ExpFunction(NumericConstant(4.394960467)).value()
        assertEquals(" 81.04142689 ", result.displayValue())
        assertEquals(81.0414268887, result.toNative())
    }
}