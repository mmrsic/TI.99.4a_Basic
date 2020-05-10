package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.test.assertEquals

class LogFunctionTest {

   @Test
   fun testThreePointFour() {
      val argument = 3.4
      val result = LogFunction(NumericConstant(argument)).value()
      assertEquals(" 1.223775432 ", result.displayValue(), "LOG($argument)")
   }

   @Test
   fun testInverseFunction() {
      val argument = 7.2
      val result = LogFunction(ExpFunction(NumericConstant(argument))).value()
      assertEquals(argument, result.toNative())
   }

}