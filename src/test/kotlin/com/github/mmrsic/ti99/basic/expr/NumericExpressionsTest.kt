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
        val result = expr.calculateToConstant()
        assertEquals(0, result.value(), "4+2^2/2-6 must yield 0")
    }

    @Test
    fun testParenthesisPlus() {
        val expr = Subtraction(Division(Exponentiation(Addition(value4, value2), value2), value2), value6)
        val result = expr.calculateToConstant()
        assertEquals(12, result.value(), "(4+2)^2/2-6 must yield 12")
    }

    @Test
    fun testParenthesisMinus() {
        val expr = Addition(value4, Division(Exponentiation(value2, value2), Subtraction(value2, value6)))
        val result = expr.calculateToConstant()
        assertEquals(3, result.value(), "4+2^2/(2-6) must yield 3")
    }
}