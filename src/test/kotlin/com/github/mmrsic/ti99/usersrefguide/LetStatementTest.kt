package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide in page II-45.
 */
class LetStatementTest {

   @Test
   fun testPrintMassEnergyEquivalence() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 LET M=1000
            110 LET C=186000
            120 E=M*C^2
            130 PRINT E
            140 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            12 to "  TI BASIC READY",
            14 to " >100 LET M=1000",
            15 to " >110 LET C=186000",
            16 to " >120 E=M*C^2",
            17 to " >130 PRINT E",
            18 to " >140 END",
            19 to " >RUN",
            20 to "   3.4596E+13",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testPrintStringVariableConcatenation() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 LET X$="HELLO, "
            110 NAME$="GENIUS!"
            120 PRINT X$;NAME$
            130 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            13 to "  TI BASIC READY",
            15 to " >100 LET X$=\"HELLO, \"",
            16 to " >110 NAME$=\"GENIUS!\"",
            17 to " >120 PRINT X$;NAME$",
            18 to " >130 END",
            19 to " >RUN",
            20 to "  HELLO, GENIUS!",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testRelationalOperator() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 LET A=20
            110 B=10
            120 LET C=A>B
            130 PRINT A;B;C
            140 C=A<B
            150 PRINT A;B;C
            160 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            9 to "  TI BASIC READY",
            11 to " >100 LET A=20",
            12 to " >110 B=10",
            13 to " >120 LET C=A>B",
            14 to " >130 PRINT A;B;C",
            15 to " >140 C=A<B",
            16 to " >150 PRINT A;B;C",
            17 to " >160 END",
            18 to " >RUN",
            19 to "   20  10 -1",
            20 to "   20  10  0",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

}