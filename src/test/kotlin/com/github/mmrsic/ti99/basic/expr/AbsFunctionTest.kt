package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.test.assertEquals

class AbsFunctionTest {

    @Test
    fun testZero() {
        val result = AbsFunction(NumericConstant(0)).value()
        assertEquals(0.0, result.toNative(), "ABS(0) must yield 0")
        assertEquals(" 0 ", result.displayValue(), "ABS(0) must yield 0")
    }

    @Test
    fun testPositiveDecimal() {
        val result = AbsFunction(NumericConstant(42.3)).value()
        assertEquals(42.3, result.toNative(), "ABS(42.3) must yield 42.3")
        assertEquals(" 42.3 ", result.displayValue(), "ABS(42.3) must yield 42.3")
    }

    @Test
    fun testNegativeDecimal() {
        val result = AbsFunction(NumericConstant(-6.124)).value()
        assertEquals(6.124, result.toNative(), "ABS(-6.124) must yield 6.124")
        assertEquals(" 6.124 ", result.displayValue(), "ABS(-6.124) must yield 6.124")
    }

    @Test
    fun testNegativeMinusExpression() {
        val result = AbsFunction(Subtraction(NumericConstant(8), NumericConstant(10))).value()
        assertEquals(2.0, result.toNative(), "ABS(8-10) must yield 2")
        assertEquals(" 2 ", result.displayValue(), "ABS(8-10) must yield 2")
    }

}