package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.test.assertEquals

class PosFunctionTest {

   @Test
   fun testPanA1() {
      val arg1 = StringConstant("PAN")
      val arg2 = StringConstant("A")
      val arg3 = NumericConstant.ONE
      val result = PosFunction(arg1, arg2, arg3).value()
      assertEquals(2.0, result.toNative(), "POS($arg1,$arg2,$arg3)")
      assertEquals(" 2 ", result.displayValue(), "POS($arg1,$arg2,$arg3)")
   }

   @Test
   fun testAPanA2() {
      val arg1 = StringConstant("APAN")
      val arg2 = StringConstant("A")
      val arg3 = NumericConstant(2)
      val result = PosFunction(arg1, arg2, arg3).value()
      assertEquals(3.0, result.toNative(), "POS($arg1,$arg2,$arg3)")
      assertEquals(" 3 ", result.displayValue(), "POS($arg1,$arg2,$arg3)")
   }

   @Test
   fun testPanA3() {
      val arg1 = StringConstant("PAN")
      val arg2 = StringConstant("A")
      val arg3 = NumericConstant(3)
      val result = PosFunction(arg1, arg2, arg3).value()
      assertEquals(0.0, result.toNative(), "POS($arg1,$arg2,$arg3)")
      assertEquals(" 0 ", result.displayValue(), "POS($arg1,$arg2,$arg3)")
   }

   @Test
   fun testPabnanAn1() {
      val arg1 = StringConstant("PABNAN")
      val arg2 = StringConstant("AN")
      val arg3 = NumericConstant.ONE
      val result = PosFunction(arg1, arg2, arg3).value()
      assertEquals(5.0, result.toNative(), "POS($arg1,$arg2,$arg3)")
      assertEquals(" 5 ", result.displayValue(), "POS($arg1,$arg2,$arg3)")
   }

}