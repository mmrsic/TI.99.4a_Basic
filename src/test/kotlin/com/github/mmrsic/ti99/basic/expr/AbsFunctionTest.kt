package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.test.assertEquals

class AbsFunctionTest {

    @Test
    fun testZero() {
        val result = AbsFunction(NumericConstant(0)).calculateToConstant()
        assertEquals(0, result.value(), "ABS(0) must yield 0")
    }

    @Test
    fun testPositiveDecimal() {
        val func = AbsFunction(NumericConstant(42.3))
        assertEquals(42.3, func.calculateToConstant().value(), "ABS(42.3) must yield 42.3")
    }

    @Test
    fun testNegativeDecimal() {
        val func = AbsFunction(NumericConstant(-6.124))
        assertEquals(6.124, func.calculateToConstant().value(), "ABS(-6.124) must yield 6.124")
    }

    @Test
    fun testNegativeMinusExpression() {
        val func = AbsFunction(Subtraction(NumericConstant(8), NumericConstant(10)))
        assertEquals(2, func.calculateToConstant().value(), "ABS(8-10) must yield 2")
    }

}