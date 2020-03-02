package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.test.assertEquals

class NumericExpressionsTest {
    private val value2 = NumericConstant(2)
    private val value4 = NumericConstant(4)
    private val value6 = NumericConstant(6)

    @Test
    fun testNaturalPrecedence() {
        val expr = Addition(value4, Subtraction(Division(Exponentiation(value2, value2), value2), value6))
        val result = expr.value()
        assertEquals(0.0, result.toNative(), "4+2^2/2-6 must yield 0")
        assertEquals(" 0 ", result.displayValue(), "4+2^2/2-6 must yield 0")
    }

    @Test
    fun testParenthesisPlus() {
        val expr = Subtraction(Division(Exponentiation(Addition(value4, value2), value2), value2), value6)
        val result = expr.value()
        assertEquals(12.0, result.toNative(), "(4+2)^2/2-6 must yield 12")
        assertEquals(" 12 ", result.displayValue(), "(4+2)^2/2-6 must yield 12")
    }

    @Test
    fun testParenthesisMinus() {
        val expr = Addition(value4, Division(Exponentiation(value2, value2), Subtraction(value2, value6)))
        val result = expr.value()
        assertEquals(3.0, result.toNative(), "4+2^2/(2-6) must yield 3")
        assertEquals(" 3 ", result.displayValue(), "4+2^2/(2-6) must yield 3")
    }

    @Test
    fun testZeroExponentiationToZero() {
        val expr = Exponentiation(NumericConstant.ZERO, NumericConstant.ZERO)
        val result = expr.value()
        assertEquals(1.0, result.toNative(), "0^0 must yield 1")
        assertEquals(" 1 ", result.displayValue(), "0^0 must yield 1")
    }

}