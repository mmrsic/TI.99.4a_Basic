package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

class BreakStatementTest {

   @Test
   fun testWithLineListAndUnbreakAndContinue() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 B=29.7
            110 BREAK 120,140
            120 H=15.8
            130 PRINT B
            140 PRINT H
            150 END
            RUN
            UNBREAK
            CONTINUE
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            5 to "  TI BASIC READY",
            7 to " >100 B=29.7",
            8 to " >110 BREAK 120,140",
            9 to " >120 H=15.8",
            10 to " >130 PRINT B",
            11 to " >140 PRINT H",
            12 to " >150 END",
            13 to " >RUN",
            15 to "  * BREAKPOINT AT 120",
            16 to " >UNBREAK",
            18 to " >CONTINUE",
            19 to "   29.7",
            20 to "   15.8",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testNoLineList() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 B=29.7
            110 BREAK
            120 H=15.8
            130 PRINT B
            140 PRINT H
            150 END
            RUN
            CONTINUE
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            7 to "  TI BASIC READY",
            9 to " >100 B=29.7",
            10 to " >110 BREAK",
            11 to " >120 H=15.8",
            12 to " >130 PRINT B",
            13 to " >140 PRINT H",
            14 to " >150 END",
            15 to " >RUN",
            17 to "  * BREAKPOINT AT 110",
            18 to " >CONTINUE",
            19 to "   29.7",
            20 to "   15.8",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testListWithBadLineNumberIsExecutedPartiallyWithWarning() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 B=29.7
            120 H=15.8
            130 PRINT B
            140 PRINT H
            150 END
            110 BREAK 125,140
            RUN
            CONTINUE
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            4 to "  TI BASIC READY",
            6 to " >100 B=29.7",
            7 to " >120 H=15.8",
            8 to " >130 PRINT B",
            9 to " >140 PRINT H",
            10 to " >150 END",
            11 to " >110 BREAK 125,140",
            12 to " >RUN",
            14 to "  * WARNING:",
            15 to "    BAD LINE NUMBER IN 110",
            16 to "   29.7",
            18 to "  * BREAKPOINT AT 140",
            19 to " >CONTINUE",
            20 to "   15.8",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

}