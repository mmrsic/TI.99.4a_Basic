package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

class TraceStatementTest {

   @Test
   fun testAllButFirstLine() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 PRINT "HI"
            110 B=27.9
            120 PRINT :B
            130 END
            105 TRACE
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            10 to "  TI BASIC READY",
            12 to """ >100 PRINT "HI"""",
            13 to " >110 B=27.9",
            14 to " >120 PRINT :B",
            15 to " >130 END",
            16 to " >105 TRACE",
            17 to " >RUN",
            18 to "  HI",
            19 to "  <110><120>",
            20 to "   27.9",
            21 to "  <130>",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }
}