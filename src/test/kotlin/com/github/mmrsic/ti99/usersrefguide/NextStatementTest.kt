package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on pages ranging from II-53 to II-55.
 */
class NextStatementTest {

   @Test
   fun testCountToTen() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 REM COUNTING FROM 1 TO  10
            110 FOR X=1 TO 10
            120 PRINT X;
            130 NEXT X
            140 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            11 to "  TI BASIC READY",
            13 to " >100 REM COUNTING FROM 1 TO",
            14 to "  10",
            15 to " >110 FOR X=1 TO 10",
            16 to " >120 PRINT X;",
            17 to " >130 NEXT X",
            18 to " >140 END",
            19 to " >RUN",
            20 to "   1  2  3  4  5  6  7  8  9",
            21 to "   10",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testRocketCountdown() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 REM ROCKET COUNTDOWN
            110 CALL CLEAR
            120 FOR I=10 TO 1 STEP -1
            130 PRINT I
            140 FOR DELAY=1 TO 200
            150 NEXT DELAY
            160 CALL CLEAR
            170 NEXT I
            180 PRINT "BLAST OFF!"
            190 REM CHANGE SCREEN COLOR
            200 FOR COLOR=2 TO 16 STEP 2
            210 CALL SCREEN(COLOR)
            220 FOR DELAY=1 TO 100
            230 NEXT DELAY
            240 NEXT COLOR
            250 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            20 to "  BLAST OFF!",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

}