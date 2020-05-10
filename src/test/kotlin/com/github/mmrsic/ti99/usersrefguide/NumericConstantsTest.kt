package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases found in User Reference Guide in section Numeric Constants on page II-9.
 */
class NumericConstantsTest {

   @Test
   fun testPrintNumberConstants() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("PRINT 1.2", machine)
      interpreter.interpret("PRINT -3", machine)
      interpreter.interpret("PRINT 0", machine)
      TestHelperScreen.assertPrintContents(
         mapOf(
            13 to "  TI BASIC READY",
            15 to " >PRINT 1.2",
            16 to "   1.2",
            18 to " >PRINT -3",
            19 to "  -3",
            21 to " >PRINT 0",
            22 to "   0",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testPrintConstantsInScientificNotationWithUnderflowAndOverflow() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("PRINT 3.264E4", machine)
      interpreter.interpret("PRINT -98.77E21", machine)
      interpreter.interpret("PRINT -9E-130", machine)
      interpreter.interpret("PRINT 9E-142", machine)
      interpreter.interpret("PRINT 97E136", machine)
      interpreter.interpret("PRINT -108E144", machine)
      TestHelperScreen.assertPrintContents(
         mapOf(
            1 to "   32640",
            3 to " >PRINT -98.77E21",
            4 to "  -9.877E+22",
            6 to " >PRINT -9E-130",
            7 to "   0",
            9 to " >PRINT 9E-142",
            10 to "   0",
            12 to " >PRINT 97E136",
            14 to "  * WARNING:",
            15 to "    NUMBER TOO BIG",
            16 to "   9.99999E+**",
            18 to " >PRINT -108E144",
            20 to "  * WARNING:",
            21 to "    NUMBER TOO BIG",
            22 to "  -9.99999E+**",
            24 to " >"
         ), machine.screen
      )
   }

}