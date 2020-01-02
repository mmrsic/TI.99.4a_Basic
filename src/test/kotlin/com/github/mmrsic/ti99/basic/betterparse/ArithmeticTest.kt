package com.github.mmrsic.ti99.basic.betterparse

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import org.junit.Test
import kotlin.test.assertEquals

class ArithmeticTest {

    @Test
    fun testIntegerConstants() {
        assertEquals(1, ArithmeticsEvaluator().parseToEnd("1"))
        assertEquals(-1, ArithmeticsEvaluator().parseToEnd("-1"))
        assertEquals(0, ArithmeticsEvaluator().parseToEnd("0"))
        assertEquals(12345, ArithmeticsEvaluator().parseToEnd("12345"))
        assertEquals(1234567890, ArithmeticsEvaluator().parseToEnd("1234567890"))
    }

}