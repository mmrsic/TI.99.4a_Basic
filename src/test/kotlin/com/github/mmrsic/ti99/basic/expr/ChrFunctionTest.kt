package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.test.assertEquals

class ChrFunctionTest {

    @Test
    fun testCode72() {
        val func = ChrFunction(NumericConstant(72))
        assertEquals("H", func.calculate(), "CHR$(72) must yield \"H\"")
    }

    @Test
    fun testCode33() {
        val func = ChrFunction(NumericConstant(33))
        assertEquals("!", func.calculate(), "CHR$(33) must yield \"!\"")
    }

    @Test
    fun testInverseFunction() {
        for (c in '!'..'~') {
            val cString = c.toString()
            val ascii = AscFunction(StringConstant(cString)).calculate()
            assertEquals(cString, ChrFunction(NumericConstant(ascii)).calculate(), "CHR$($ascii)")
        }
    }


}