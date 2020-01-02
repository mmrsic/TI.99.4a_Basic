package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.test.assertEquals

class ExpFunctionTest {

    @Test
    fun testExpSeven() {
        val func = ExpFunction(NumericConstant(7))
        assertEquals(" 1096.633158 ", func.calculateToConstant().displayValue(), "EXP(7)")
    }

    @Test
    fun testExpFourSomething() {
        val func = ExpFunction(NumericConstant(4.394960467))
        assertEquals(" 81.04142689 ", func.calculateToConstant().displayValue(), "EXP(7)")
    }
}