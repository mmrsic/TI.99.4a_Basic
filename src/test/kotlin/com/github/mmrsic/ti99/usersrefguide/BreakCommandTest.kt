package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on pages from II-30 to II-32.
 */
class BreakCommandTest {

   @Test
   fun testBreakpointAtBeginningOfLine() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 A=26.7
            110 C=19.3
            120 PRINT A
            130 PRINT C
            140 END
            BREAK 110
            RUN
            LIST 110
            PRINT A;C
            A=5.8
            PRINT A
            CONTINUE
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            1 to " >130 PRINT C",
            2 to " >140 END",
            3 to " >BREAK 110",
            5 to " >RUN",
            7 to "  * BREAKPOINT AT 110",
            8 to " >LIST 110",
            9 to "  110 C=19.3",
            10 to " >PRINT A;C",
            11 to "   26.7  0",
            13 to " >A=5.8",
            15 to " >PRINT A",
            16 to "   5.8",
            18 to " >CONTINUE",
            19 to "   5.8",
            20 to "   19.3",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testBreakpointIsRemovedOnBreak() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 A=26.7
            110 C=19.3
            120 PRINT A
            130 PRINT C
            140 END
            BREAK 110
            RUN 
            BREAK 120
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            7 to "  TI BASIC READY",
            9 to " >100 A=26.7",
            10 to " >110 C=19.3",
            11 to " >120 PRINT A",
            12 to " >130 PRINT C",
            13 to " >140 END",
            14 to " >BREAK 110",
            16 to " >RUN",
            18 to "  * BREAKPOINT AT 110",
            19 to " >BREAK 120",
            21 to " >RUN",
            23 to "  * BREAKPOINT AT 120",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testContinueImpossibleAfterProgramChange() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 A=26.7
            110 C=19.3
            120 PRINT A
            130 PRINT C
            140 END
            BREAK 120
            RUN
            110
            CONTINUE
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            8 to "  TI BASIC READY",
            10 to " >100 A=26.7",
            11 to " >110 C=19.3",
            12 to " >120 PRINT A",
            13 to " >130 PRINT C",
            14 to " >140 END",
            15 to " >BREAK 120",
            17 to " >RUN",
            19 to "  * BREAKPOINT AT 120",
            20 to " >110",
            21 to " >CONTINUE",
            22 to "  * CAN'T CONTINUE",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testRemoveBreakpointsWithUnbreak() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 A=26.7
            110 C=19.3
            120 PRINT A
            130 PRINT C
            140 END
            BREAK 110,130
            RUN
            UNBREAK
            CONTINUE
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            1 to " >110 C=19.3",
            2 to " >120 PRINT A",
            3 to " >130 PRINT C",
            4 to " >140 END",
            5 to " >BREAK 110,130",
            7 to " >RUN",
            9 to "  * BREAKPOINT AT 110",
            10 to " >UNBREAK",
            12 to " >CONTINUE",
            13 to "   26.7",
            14 to "   19.3",
            16 to "  ** DONE **",
            18 to " >RUN",
            19 to "   26.7",
            20 to "   19.3",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testCharactersAreResetAtBreakpoint() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("100 CALL CLEAR", machine)
      interpreter.interpret("110 CALL CHAR(42,\"FFFFFFFFFFFFFFFF\")", machine)
      interpreter.interpret(" 120 CALL HCHAR(12,12,42,10)", machine)
      interpreter.interpret("130 FOR I=1 TO 500", machine)
      interpreter.interpret("140 NEXT I", machine)
      interpreter.interpret("150 END", machine)
      interpreter.interpret("BREAK 150", machine)
      interpreter.interpret("RUN", machine)
      interpreter.interpret("CONTINUE", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            6 to "           **********",
            19 to "  * BREAKPOINT AT 150",
            20 to " >CONTINUE",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
      TestHelperScreen.assertCursorAt(24, 3, machine.screen)
   }

   @Test
   fun testListWithBadLineNumberIsIgnored() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 B=29.7
            120 H=15.8
            130 PRINT B
            140 PRINT H
            150 END
            BREAK 120,130140
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            7 to "  TI BASIC READY",
            9 to " >100 B=29.7",
            10 to " >120 H=15.8",
            11 to " >130 PRINT B",
            12 to " >140 PRINT H",
            13 to " >150 END",
            14 to " >BREAK 120,130140",
            16 to "  * BAD LINE NUMBER",
            18 to " >RUN",
            19 to "   29.7",
            20 to "   15.8",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

}