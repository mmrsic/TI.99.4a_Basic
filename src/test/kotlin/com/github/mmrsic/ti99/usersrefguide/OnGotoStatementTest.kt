package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.KeyboardInputProvider
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on page II-50.
 */
class OnGotoStatementTest {

   @Test
   fun testHowDoesOnGotoWork() {
      val machine = TiBasicModule().apply {
         setKeyboardInputProvider(object : KeyboardInputProvider {
            override fun provideInput(ctx: KeyboardInputProvider.InputContext): Sequence<Char> {
               println("Providing input for overall call #${ctx.overallCalls}")
               return when (ctx.overallCalls) {
                  1 -> "2\r".asSequence()
                  2 -> "1.2\r".asSequence()
                  3 -> "3.7\r".asSequence()
                  4 -> "6\r".asSequence()
                  else -> super.provideInput(ctx)
               }
            }
         })
      }
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
                100 REM HOW DOES ON-GOTO    WORK?
                110 INPUT X
                120 ON X GOTO 130,150,170,190,210
                130 PRINT "X=1"
                140 GOTO 110
                150 PRINT "X=2"
                160 GOTO 110
                170 PRINT "X=3"
                180 GOTO 110
                190 PRINT "X=4"
                200 GOTO 110
                210 END
                RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            1 to " >110 INPUT X",
            2 to " >120 ON X GOTO 130,150,170,19",
            3 to "  0,210",
            4 to " >130 PRINT \"X=1\"",
            5 to " >140 GOTO 110",
            6 to " >150 PRINT \"X=2\"",
            7 to " >160 GOTO 110",
            8 to " >170 PRINT \"X=3\"",
            9 to " >180 GOTO 110",
            10 to " >190 PRINT \"X=4\"",
            11 to " >200 GOTO 110",
            12 to " >210 END",
            13 to " >RUN",
            14 to "  ? 2",
            15 to "  X=2",
            16 to "  ? 1.2",
            17 to "  X=1",
            18 to "  ? 3.7",
            19 to "  X=4",
            20 to "  ? 6",
            22 to "  * BAD VALUE IN 120",
            24 to " >"
         ), machine.screen
      )
   }

}