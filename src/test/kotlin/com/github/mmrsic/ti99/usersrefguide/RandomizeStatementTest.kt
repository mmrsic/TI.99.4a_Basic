package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on page II-95.
 */
class RandomizeStatementTest {

   @Test
   fun testWithSeed() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 RANDOMIZE 23
            110 FOR I=1 TO 5
            120 PRINT INT(10*RND)+1
            130 NEXT I
            140 STOP
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            8 to "  TI BASIC READY",
            10 to " >100 RANDOMIZE 23",
            11 to " >110 FOR I=1 TO 5",
            12 to " >120 PRINT INT(10*RND)+1",
            13 to " >130 NEXT I",
            14 to " >140 STOP",
            15 to " >RUN",
            16 to "   6",
            17 to "   4",
            18 to "   3",
            19 to "   8",
            20 to "   8",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }
}