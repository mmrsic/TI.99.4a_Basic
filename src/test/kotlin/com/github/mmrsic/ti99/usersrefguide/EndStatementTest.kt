package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on page II-47.
 */
class EndStatementTest {

   @Test
   fun testLastStatementInProgram() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 A=10
            110 B=20
            120 C=A*B
            130 PRINT C
            140 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            12 to "  TI BASIC READY",
            14 to " >100 A=10",
            15 to " >110 B=20",
            16 to " >120 C=A*B",
            17 to " >130 PRINT C",
            18 to " >140 END",
            19 to " >RUN",
            20 to "   200",
            22 to "  ** DONE **",
            24 to " >"
         ),
         machine.screen
      )
   }

}