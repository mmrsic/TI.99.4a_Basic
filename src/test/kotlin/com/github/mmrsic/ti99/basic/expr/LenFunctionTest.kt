package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.test.assertEquals

class LenFunctionTest {

    @Test
    fun testAbcde() {
        val argument = "ABCDE"
        val result = LenFunction(StringConstant(argument)).value()
        assertEquals(5.0, result.toNative(), "LEN($argument)")
        assertEquals(" 5 ", result.displayValue(), "LEN($argument)")
    }

    @Test
    fun testThisIsASentence() {
        val argument = "THIS IS A SENTENCE."
        val result = LenFunction(StringConstant(argument)).value()
        assertEquals(19.0, result.toNative(), "LEN($argument)")
        assertEquals(" 19 ", result.displayValue(), "LEN($argument)")
    }

    @Test
    fun testEmpty() {
        val argument = ""
        val result = LenFunction(StringConstant(argument)).value()
        assertEquals(0.0, result.toNative(), "LEN($argument)")
        assertEquals(" 0 ", result.displayValue(), "LEN($argument)")
    }

    @Test
    fun testSpace() {
        val argument = " "
        val func = LenFunction(StringConstant(argument)).value()
        assertEquals(1.0, func.toNative(), "LEN($argument)")
        assertEquals(" 1 ", func.displayValue(), "LEN($argument)")
    }

}