package com.github.mmrsic.ti99.extbasicbook

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * _ATN(numeric-expression)_
 *
 * The ATN function returns the measure of the angle (in radians) whose tangent is numeric-expression. If you want the
 * equivalent angle in degrees, multiply by 180/PI.
 * The value given by the ATN function is always in the range -PI/2 < ATN(X) < PI/2.
 */
class AtnFunctionTest {

   /** PRINT ATN(0) prints 0. */
   @Test
   fun testPrintValueForZeroArgument() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 PRINT ATN(0)
         RUN
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            16 to "  TI BASIC READY",
            18 to " >100 PRINT ATN(0)",
            19 to " >RUN",
            20 to "   0",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   /** Q=ATN(.44) sets Q equal to .4145068746. */
   @Test
   fun testAssignment() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 Q=ATN(.44)
         RUN
         PRINT Q
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            14 to "  TI BASIC READY",
            16 to " >100 Q=ATN(.44)",
            17 to " >RUN",
            19 to "  ** DONE **",
            21 to " >PRINT Q",
            22 to "   .4145068746",
            24 to " >"
         ), machine.screen
      )
   }
}