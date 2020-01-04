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
        val result = TiBasicParser(TiBasicModule()).parseToEnd("A=2")
        assertTrue(result is LetNumberStatement)
        assertEquals("A", result.varName)
        assertEquals(2, result.expr.calculateToConstant().value())
    }
}