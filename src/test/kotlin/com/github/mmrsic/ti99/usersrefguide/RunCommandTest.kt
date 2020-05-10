package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases found in User Reference Guide in section Run Command on page II-23.
 */
class RunCommandTest {

   @Test
   fun testExamples() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
                RUN
                100 A=-16
                110 B=25
                120 PRINT A;B
                130 END
                RUN
                RUN 110
                RUN 115
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            1 to "  TI BASIC READY",
            3 to " >RUN",
            5 to "  * CAN'T DO THAT",
            7 to " >100 A=-16",
            8 to " >110 B=25",
            9 to " >120 PRINT A;B",
            10 to " >130 END",
            11 to " >RUN",
            12 to "  -16  25",
            14 to "  ** DONE **",
            16 to " >RUN 110",
            17 to "   0  25",
            19 to "  ** DONE **",
            21 to " >RUN 115",
            22 to "  * BAD LINE NUMBER",
            24 to " >"
         ), machine.screen
      )
   }

}