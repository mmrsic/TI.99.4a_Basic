package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.test.assertEquals

class SgnFunctionTest {

   @Test
   fun testFivePointEight() {
      val argument = NumericConstant(5.8)
      val result = SgnFunction(argument).value()
      assertEquals(1.0, result.toNative(), "SGN($argument)")
      assertEquals(" 1 ", result.displayValue(), "SGN($argument)")
   }

   @Test
   fun testMinusFive() {
      val argument = NumericConstant(-5)
      val result = SgnFunction(argument).value()
      assertEquals(-1.0, result.toNative(), "SGN($argument)")
      assertEquals("-1 ", result.displayValue(), "SGN($argument)")
   }

   @Test
   fun testZero() {
      val argument = NumericConstant.ZERO
      val result = SgnFunction(argument).value()
      assertEquals(0.0, result.toNative(), "SGN($argument)")
      assertEquals(" 0 ", result.displayValue(), "SGN($argument)")
   }

}