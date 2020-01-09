package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.test.assertEquals

class AscFunctionTest {

    @Test
    fun testCharA() {
        assertEquals(65, AscFunction(StringConstant("A")).value().toNative().toInt())
    }

    @Test
    fun testCharC() {
        assertEquals(67, AscFunction(StringConstant("C")).value().toNative().toInt())
    }

    @Test
    fun testChar1() {
        assertEquals(49, AscFunction(StringConstant("1")).value().toNative().toInt())
    }

    @Test
    fun testStringHello() {
        assertEquals(72, AscFunction(StringConstant("HELLO")).value().toNative().toInt())
    }

    @Test
    fun testStringGutenTag() {
        assertEquals(71, AscFunction(StringConstant("GUTEN TAG")).value().toNative().toInt())
    }

    @Test
    fun testInverseFunction() {
        for (ascii in 30..128) {
            val c = ChrFunction(NumericConstant(ascii)).value().toNative()
            assertEquals(ascii, AscFunction(StringConstant(c)).value().toNative().toInt(), "ASC($c)")
        }
    }

}