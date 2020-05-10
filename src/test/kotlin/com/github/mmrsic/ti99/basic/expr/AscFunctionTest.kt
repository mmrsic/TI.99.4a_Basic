package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.test.assertEquals

class AscFunctionTest {

   @Test
   fun testCharA() {
      val result = AscFunction(StringConstant("A")).value()
      assertEquals(65, result.toNative().toInt())
      assertEquals(" 65 ", result.displayValue())
   }

   @Test
   fun testCharC() {
      val result = AscFunction(StringConstant("C")).value()
      assertEquals(67, result.toNative().toInt())
      assertEquals(" 67 ", result.displayValue())
   }

   @Test
   fun testChar1() {
      val result = AscFunction(StringConstant("1")).value()
      assertEquals(49, result.toNative().toInt())
      assertEquals(" 49 ", result.displayValue())
   }

   @Test
   fun testStringHello() {
      val result = AscFunction(StringConstant("HELLO")).value()
      assertEquals(72, result.toNative().toInt())
      assertEquals(" 72 ", result.displayValue())
   }

   @Test
   fun testStringGutenTag() {
      val result = AscFunction(StringConstant("GUTEN TAG")).value()
      assertEquals(71, result.toNative().toInt())
      assertEquals(" 71 ", result.displayValue())
   }

   @Test
   fun testInverseFunction() {
      for (ascii in 30..128) {
         val c = ChrFunction(NumericConstant(ascii)).value().toNative()
         assertEquals(ascii, AscFunction(StringConstant(c)).value().toNative().toInt(), "ASC($c)")
      }
   }

}