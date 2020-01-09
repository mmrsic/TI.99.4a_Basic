package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.test.assertEquals

class ChrFunctionTest {

    @Test
    fun testCode72() {
        val result = ChrFunction(NumericConstant(72)).value()
        assertEquals("H", result.toNative(), "CHR$(72) must yield \"H\"")
        assertEquals("H", result.displayValue(), "CHR$(72) must yield \"H\"")
    }

    @Test
    fun testCode33() {
        val result = ChrFunction(NumericConstant(33)).value()
        assertEquals("!", result.toNative(), "CHR$(33) must yield \"!\"")
        assertEquals("!", result.displayValue(), "CHR$(33) must yield \"!\"")
    }

    @Test
    fun testInverseFunction() {
        for (c in '!'..'~') {
            val cString = c.toString()
            val ascii = AscFunction(StringConstant(cString)).value()
            assertEquals(cString, ChrFunction(NumericConstant(ascii.toNative())).value().displayValue(), "CHR$($ascii)")
        }
    }


}