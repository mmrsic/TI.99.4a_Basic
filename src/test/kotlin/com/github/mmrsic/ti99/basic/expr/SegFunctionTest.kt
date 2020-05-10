package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.test.assertEquals

class SegFunctionTest {

   @Test
   fun testFirstnameLastnameOneNine() {
      val arg1 = StringConstant("FIRSTNAME LASTNAME")
      val arg2 = NumericConstant.ONE
      val arg3 = NumericConstant(9)
      val result = SegFunction(arg1, arg2, arg3).value()
      assertEquals("FIRSTNAME", result.toNative())
      assertEquals("FIRSTNAME", result.displayValue())
   }

   @Test
   fun testFirstnameLastnameElevenEight() {
      val arg1 = StringConstant("FIRSTNAME LASTNAME")
      val arg2 = NumericConstant(11)
      val arg3 = NumericConstant(8)
      val result = SegFunction(arg1, arg2, arg3).value()
      assertEquals("LASTNAME", result.toNative())
      assertEquals("LASTNAME", result.displayValue())
   }

   @Test
   fun testFirstnameLastnameTenOne() {
      val arg1 = StringConstant("FIRSTNAME LASTNAME")
      val arg2 = NumericConstant(10)
      val arg3 = NumericConstant.ONE
      val result = SegFunction(arg1, arg2, arg3).value()
      assertEquals(" ", result.toNative())
      assertEquals(" ", result.displayValue())
   }

}