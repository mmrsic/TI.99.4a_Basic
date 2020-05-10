package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on page II-36.
 */
class TraceCommandTest {

   @Test
   fun testEntireProgram() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 PRINT "HI"
            110 B=27.9
            120 PRINT :B
            130 END
            TRACE
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            9 to "  TI BASIC READY",
            11 to """ >100 PRINT "HI"""",
            12 to " >110 B=27.9",
            13 to " >120 PRINT :B",
            14 to " >130 END",
            15 to " >TRACE",
            17 to " >RUN",
            18 to "  <100>HI",
            19 to "  <110><120>",
            20 to "   27.9",
            21 to "  <130>",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }
}