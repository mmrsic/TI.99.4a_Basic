package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on page II-72.
 */
class CallClearTest {

   @Test
   fun testCommand() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
                PRINT "HELLO THERE!"
                CALL CLEAR
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(mapOf(24 to " >"), machine.screen)
      TestHelperScreen.assertCursorAt(24, 3, machine.screen)
   }

   @Test
   fun testAsFirstStatementWithPrintsAfterwards() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 CALL CLEAR
            110 PRINT "HELLO THERE!"
            120 PRINT "HOW ARE YOU?"
            130 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            19 to "  HELLO THERE!",
            20 to "  HOW ARE YOU?",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
      TestHelperScreen.assertCursorAt(24, 3, machine.screen)
   }

   @Test
   fun testRedefinedSpaceCharacter() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 CALL CHAR(32,"0103070F1F3F7FFF")
            110 CALL CLEAR
            120 GOTO 120
            """.trimIndent(), machine
      )
      machine.addProgramLineHookAfter({ line -> line.lineNumber == 110 }, { tiBasicModule ->
         TestHelperScreen.assertAllPatternsEqual("0103070F1F3F7FFF", machine.screen)
         machine.addBreakpoint(120)
      })

      interpreter.interpret("RUN", machine)
   }

}