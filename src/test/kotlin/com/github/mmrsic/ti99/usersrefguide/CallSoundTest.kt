package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on pages II-84 and II-85.
 */
class CallSoundTest {

   @Test
   fun testCommandTenthOfASecond() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("CALL SOUND(100,294,2)", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            20 to "  TI BASIC READY",
            22 to " >CALL SOUND(100,294,2)",
            24 to " >"
         ), machine.screen
      )
      TestHelperScreen.assertCursorAt(24, 3, machine.screen)
   }

   @Test
   fun testRepeatedStatementWithNegativeDuration() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 TONE=110
            110 FOR COUNT=1 TO 10
            120 CALL SOUND(-500,TONE,1)
            130 TONE=TONE+110
            140 NEXT COUNT
            150 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            12 to "  TI BASIC READY",
            14 to " >100 TONE=110",
            15 to " >110 FOR COUNT=1 TO 10",
            16 to " >120 CALL SOUND(-500,TONE,1)",
            17 to " >130 TONE=TONE+110",
            18 to " >140 NEXT COUNT",
            19 to " >150 END",
            20 to " >RUN",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testRepeatedStatementWithPositiveDuration() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 TONE=110
            110 FOR COUNT=1 TO 10
            120 CALL SOUND(+500,TONE,1)
            130 TONE=TONE+110
            140 NEXT COUNT
            150 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            12 to "  TI BASIC READY",
            14 to " >100 TONE=110",
            15 to " >110 FOR COUNT=1 TO 10",
            16 to " >120 CALL SOUND(+500,TONE,1)",
            17 to " >130 TONE=TONE+110",
            18 to " >140 NEXT COUNT",
            19 to " >150 END",
            20 to " >RUN",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testToneAndNoiseCommands() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            CALL SOUND(1000,440,2)
            CALL SOUND(500,-1,2)
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            18 to "  TI BASIC READY",
            20 to " >CALL SOUND(1000,440,2)",
            22 to " >CALL SOUND(500,-1,2)",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testAllNoisesAsStatements() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 FOR NOISE=-1 TO -8 STEP -1
            110 CALL SOUND(1000,NOISE,2)
            120 NEXT NOISE
            130 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            12 to "  TI BASIC READY",
            14 to " >100 FOR NOISE=-1 TO -8 STEP",
            15 to "  -1",
            16 to " >110 CALL SOUND(1000,NOISE,2)",
            18 to " >120 NEXT NOISE",
            19 to " >130 END",
            20 to " >RUN",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testThreeTonesAndANoiseCommand() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("CALL SOUND(2500,440,2,659,5,880,10,-6,15)", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            19 to "  TI BASIC READY",
            21 to " >CALL SOUND(2500,440,2,659,5,",
            22 to "  880,10,-6,15)",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testCMajorChordCommand() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            DUR=2500
            VOL=2
            C=262
            E=330
            G=392
            CALL SOUND(DUR,C,VOL,E,VOL,G,VOL)
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            9 to "  TI BASIC READY",
            11 to " >DUR=2500",
            13 to " >VOL=2",
            15 to " >C=262",
            17 to " >E=330",
            19 to " >G=392",
            21 to " >CALL SOUND(DUR,C,VOL,E,VOL,G",
            22 to "  ,VOL)",
            24 to " >"
         ), machine.screen
      )
   }

}