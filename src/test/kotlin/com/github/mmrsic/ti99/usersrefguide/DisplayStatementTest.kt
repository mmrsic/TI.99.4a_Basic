package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on page II-70.
 */
class DisplayStatementTest {

   @Test
   fun testSameAsPrint() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 A=35.6
            110 B$="HI!!"
            120 C=49.7
            130 PRINT B$:A;C 
            140 DISPLAY B$:A;C 
            150 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            8 to "  TI BASIC READY",
            10 to " >100 A=35.6",
            11 to " >110 B$=\"HI!!\"",
            12 to " >120 C=49.7",
            13 to " >130 PRINT B$:A;C",
            14 to " >140 DISPLAY B$:A;C",
            15 to " >150 END",
            16 to " >RUN",
            17 to "  HI!!",
            18 to "   35.6  49.7",
            19 to "  HI!!",
            20 to "   35.6  49.7",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

}