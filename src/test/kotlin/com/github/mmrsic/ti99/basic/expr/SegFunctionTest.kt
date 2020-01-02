package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.test.assertEquals

class SegFunctionTest {

    @Test
    fun testFirstnameLastnameOneNine() {
        val arg1 = StringConstant("FIRSTNAME LASTNAME")
        val arg2 = NumericConstant(1)
        val arg3 = NumericConstant(9)
        val func = SegFunction(arg1, arg2, arg3)
        assertEquals("FIRSTNAME", func.calculate())
    }

    @Test
    fun testFirstnameLastnameElevenEight() {
        val arg1 = StringConstant("FIRSTNAME LASTNAME")
        val arg2 = NumericConstant(11)
        val arg3 = NumericConstant(8)
        val func = SegFunction(arg1, arg2, arg3)
        assertEquals("LASTNAME", func.calculate())
    }

    @Test
    fun testFirstnameLastnameTenOne() {
        val arg1 = StringConstant("FIRSTNAME LASTNAME")
        val arg2 = NumericConstant(10)
        val arg3 = NumericConstant(1)
        val func = SegFunction(arg1, arg2, arg3)
        assertEquals(" ", func.calculate())
    }

}