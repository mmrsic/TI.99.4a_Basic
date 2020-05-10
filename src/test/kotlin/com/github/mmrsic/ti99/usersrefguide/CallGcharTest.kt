package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on page II-86.
 */
class CallGcharTest {

   @Test
   fun testStatement() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 CALL CLEAR
            110 CALL HCHAR(1,1,36,768)
            120 CALL GCHAR(5,10,X)
            130 CALL CLEAR
            140 PRINT X
            150 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            20 to "   36",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

}