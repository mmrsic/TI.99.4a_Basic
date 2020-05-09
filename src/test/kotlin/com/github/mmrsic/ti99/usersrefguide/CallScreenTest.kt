package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.Breakpoint
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.KeyboardInputProvider
import com.github.mmrsic.ti99.hw.TiBasicModule
import com.github.mmrsic.ti99.hw.TiCharacterColor
import com.github.mmrsic.ti99.hw.TiColor
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on page II-75.
 */
class CallScreenTest {

   @Test
   fun testAskForScreenColor() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 CALL CLEAR
            110 INPUT "SCREEN COLOR?":S
            120 INPUT "FOREGROUND?":F
            130 INPUT "BACKGROUND?":B
            140 CALL CLEAR
            150 CALL SCREEN(S)
            160 CALL COLOR(2,F,B)
            170 CALL HCHAR(12,3,42,28)
            180 GOTO 110
            """.trimIndent(), machine
      )
      machine.setKeyboardInputProvider(object : KeyboardInputProvider {
         override fun provideInput(ctx: KeyboardInputProvider.InputContext): Sequence<Char> {
            if (ctx.programLineCalls > 1) throw Breakpoint()
            return (when (ctx.prompt) {
               "SCREEN COLOR?" -> "7"
               "FOREGROUND?" -> "13"
               "BACKGROUND?" -> "16"
               else -> throw NotImplementedError("Test case cannot handle prompt: ${ctx.prompt}")
            } + "\r").asSequence()
         }
      })
      machine.addProgramLineHookAfter({ line -> line.lineNumber == 170 }) {
         TestHelperScreen.assertColors({ r, c, charColor ->
            if (r == 12 && c in 3..30) {
               charColor == TiCharacterColor(TiColor.DarkGreen, TiColor.White)
            } else {
               charColor == TiCharacterColor(TiColor.Black, TiColor.DarkRed)
            }
         }, machine.screen)
         TestHelperScreen.assertPrintContents(mapOf(12 to "  " + "*".repeat(28)), machine.screen)
      }
      interpreter.interpret("RUN", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            10 to "  " + "*".repeat(28),
            22 to "  SCREEN COLOR?",
            23 to "  * BREAKPOINT AT 110",
            24 to " >"
         ), machine.screen
      )
      TestHelperScreen.assertCursorAt(24, 3, machine.screen)
      TestHelperScreen.assertAllColorsEqual(TiCharacterColor(TiColor.Black, TiColor.Cyan), machine.screen)
   }

}