package com.github.mmrsic.ti99.basic.stmt

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.mmrsic.ti99.basic.LetNumberStatement
import com.github.mmrsic.ti99.basic.betterparse.TiBasicParser
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LetNumberStatementTest {

    @Test
    fun testAssignTwoToA() {
        val result = parseLetNumberStatement("A=2")
        assertEquals("A", result.varName)
        assertEquals(2, result.expr.calculateToConstant().value())
    }

    @Test
    fun testAssignPlusConstants() {
        val result = parseLetNumberStatement("A=1+2")
        assertEquals("A", result.varName)
        assertEquals(3.0, result.expr.calculate())
    }

    @Test
    fun testAssignMinusConstants() {
        val result = parseLetNumberStatement("B=4-3")
        assertEquals("B", result.varName)
        assertEquals(1.0, result.expr.calculate())
    }

    @Test
    fun testAssignMultiplicationConstants() {
        val result = parseLetNumberStatement("C=5*6")
        assertEquals("C", result.varName)
        assertEquals(30.0, result.expr.calculate())
    }

    @Test
    fun testAssignDivisionConstants() {
        val result = parseLetNumberStatement("D=7/8")
        assertEquals("D", result.varName)
        assertEquals(0.875, result.expr.calculate())
    }

    @Test
    fun testAssignPlusChain() {
        val result = parseLetNumberStatement("E=1+2+3+4+5")
        assertEquals("E", result.varName)
        assertEquals(15.0, result.expr.calculate())
    }

    // HELPERS //

    private fun parseLetNumberStatement(input: String): LetNumberStatement {
        val result = TiBasicParser(TiBasicModule()).parseToEnd(input)
        assertTrue(result is LetNumberStatement)
        return result
    }

}