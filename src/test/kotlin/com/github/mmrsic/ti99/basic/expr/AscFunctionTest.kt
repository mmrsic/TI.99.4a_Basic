package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.test.assertEquals

class AscFunctionTest {

    @Test
    fun testCharA() {
        assertEquals(65, AscFunction(StringConstant("A")).calculate())
    }

    @Test
    fun testCharC() {
        assertEquals(67, AscFunction(StringConstant("C")).calculate())
    }

    @Test
    fun testChar1() {
        assertEquals(49, AscFunction(StringConstant("1")).calculate())
    }

    @Test
    fun testStringHello() {
        assertEquals(72, AscFunction(StringConstant("HELLO")).calculate())
    }

    @Test
    fun testStringGutenTag() {
        assertEquals(71, AscFunction(StringConstant("GUTEN TAG")).calculate())
    }

    @Test
    fun testInverseFunction() {
        for (ascii in 30..128) {
            val c = ChrFunction(NumericConstant(ascii)).calculate()
            assertEquals(ascii, AscFunction(StringConstant(c)).calculate(), "ASC($c)")
        }
    }

}