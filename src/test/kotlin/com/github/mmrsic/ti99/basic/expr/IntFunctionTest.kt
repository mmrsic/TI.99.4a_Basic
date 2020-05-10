package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.test.assertEquals

class IntFunctionTest {

   @Test
   fun testThreePointFour() {
      val argument = 3.4
      val result = IntFunction(NumericConstant(argument)).value()
      assertEquals(3.0, result.toNative(), "INT($argument)")
      assertEquals(" 3 ", result.displayValue(), "INT($argument)")
   }

   @Test
   fun testThreePointNineZero() {
      val argument = 3.90
      val result = IntFunction(NumericConstant(argument)).value()
      assertEquals(3.0, result.toNative(), "INT($argument)")
      assertEquals(" 3 ", result.displayValue(), "INT($argument)")
   }

   @Test
   fun testThreePointNineNineNineo() {
      val argument = 3.9999999999
      val result = IntFunction(NumericConstant(argument)).value()
      assertEquals(3.0, result.toNative(), "INT($argument)")
      assertEquals(" 3 ", result.displayValue(), "INT($argument)")
   }

   @Test
   fun testFourPointZero() {
      val argument = 4.0
      val result = IntFunction(NumericConstant(argument)).value()
      assertEquals(4.0, result.toNative(), "INT($argument)")
      assertEquals(" 4 ", result.displayValue(), "INT($argument)")
   }

   @Test
   fun testMinusThreePointNine() {
      val argument = -3.9
      val result = IntFunction(NumericConstant(argument)).value()
      assertEquals(-4.0, result.toNative(), "INT($argument)")
      assertEquals("-4 ", result.displayValue(), "INT($argument)")
   }

   @Test
   fun testNearlyMinusThree() {
      val argument = -3.0000001
      val result = IntFunction(NumericConstant(argument)).value()
      assertEquals(-4.0, result.toNative(), "INT($argument)")
      assertEquals("-4 ", result.displayValue(), "INT($argument)")
   }

   @Test
   fun testFivePointSomething() {
      val argument = 5.87353
      val result = IntFunction(NumericConstant(argument)).value()
      assertEquals(5.0, result.toNative(), "INT($argument)")
      assertEquals(" 5 ", result.displayValue(), "INT($argument)")
   }

   @Test
   fun testMinusFourPointSomething() {
      val argument = -4.35212
      val func = IntFunction(NumericConstant(argument)).value()
      assertEquals(-5.0, func.toNative(), "INT($argument)")
      assertEquals("-5 ", func.displayValue(), "INT($argument)")
   }

   @Test
   fun testNearlyOne() {
      val argument = .99999999
      val result = IntFunction(NumericConstant(argument)).value()
      assertEquals(0.0, result.toNative(), "INT($argument)")
      assertEquals(" 0 ", result.displayValue(), "INT($argument)")
   }

   @Test
   fun testMinusNearlyZero() {
      val argument = -.00000001
      val result = IntFunction(NumericConstant(argument)).value()
      assertEquals(-1.0, result.toNative(), "INT($argument)")
      assertEquals("-1 ", result.displayValue(), "INT($argument)")
   }

}