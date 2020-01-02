package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.test.assertEquals

class LenFunctionTest {

    @Test
    fun testAbcde() {
        val argument = "ABCDE"
        val func = LenFunction(StringConstant(argument))
        assertEquals(5, func.calculate(), "LEN($argument)")
    }

    @Test
    fun testThisIsASentence() {
        val argument = "THIS IS A SENTENCE."
        val func = LenFunction(StringConstant(argument))
        assertEquals(19, func.calculate(), "LEN($argument)")
    }

    @Test
    fun testEmpty() {
        val argument = ""
        val func = LenFunction(StringConstant(argument))
        assertEquals(0, func.calculate(), "LEN($argument)")
    }

    @Test
    fun testSpace() {
        val argument = " "
        val func = LenFunction(StringConstant(argument))
        assertEquals(1, func.calculate(), "LEN($argument)")
    }

}