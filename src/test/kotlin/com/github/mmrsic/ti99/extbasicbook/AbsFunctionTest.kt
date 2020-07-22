package com.github.mmrsic.ti99.extbasicbook

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * _ABS(numeric-expression)_
 *
 * The ABS function gives the absolute value of numeric-expression. If numeric-expression is positive, ABS gives
 * the value of numeric-expression. If numeric-expression is negative, ABS gives its negative (a positive number).
 * If numeric-expression is zero, ABS returns zero. The result of ABS is always a non-negative number.
 */
class AbsFunctionTest {

   /** PRINT ABS(42.3) prints 42.3. */
   @Test
   fun testPositiveValue() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 PRINT ABS(42.3)
         RUN
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            16 to "  TI BASIC READY",
            18 to " >100 PRINT ABS(42.3)",
            19 to " >RUN",
            20 to "   42.3",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   /** VV=ABS(-6.124) sets VV equal to 6.124. */
   @Test
   fun testNegativeValue() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 VV=ABS(-6.124)
         RUN
         PRINT VV
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            14 to "  TI BASIC READY",
            16 to " >100 VV=ABS(-6.124)",
            17 to " >RUN",
            19 to "  ** DONE **",
            21 to " >PRINT VV",
            22 to "   6.124",
            24 to " >"
         ), machine.screen
      )
   }
}