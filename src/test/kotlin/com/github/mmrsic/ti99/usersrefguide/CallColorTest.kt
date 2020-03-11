package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.Breakpoint
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.CodeSequenceProvider
import com.github.mmrsic.ti99.hw.TiBasicModule
import com.github.mmrsic.ti99.hw.TiCharacterColor
import com.github.mmrsic.ti99.hw.TiColor
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on pages II-73 and II-74.
 */
class CallColorTest {

    @Test
    fun testInputForegroundAndBackground() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 CALL CLEAR
            110 INPUT "FOREGROUND?":F
            120 INPUT "BACKGROUND?":B
            130 CALL CLEAR
            140 CALL COLOR(2,F,B)
            150 CALL HCHAR(12,3,42,28)
            160 GO TO 110
            """.trimIndent(), machine
        )
        machine.setKeyboardInputProvider(object : CodeSequenceProvider {
            override fun provideInput(ctx: CodeSequenceProvider.Context): Sequence<Char> {
                if (ctx.programLineCalls == 2) throw Breakpoint()
                return (when (ctx.programLine) {
                    110 -> "2"
                    120 -> "14"
                    else -> throw IllegalArgumentException("Cannot provide input for line ${ctx.programLine}")
                } + "\r").asSequence()
            }
        })
        machine.addProgramLineHookAfter({ line -> line.lineNumber == 160 }, { _ ->
            TestHelperScreen.assertColors({ row, col, charColors ->
                if (row == 12 && col in 3..30) {
                    charColors == TiCharacterColor(TiColor.Black, TiColor.Magenta)
                } else {
                    charColors == TiCharacterColor(TiColor.Black, TiColor.LightGreen)
                }
            }, machine.screen)
        })
        interpreter.interpret("RUN", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                10 to "  " + "*".repeat(28),
                22 to "  FOREGROUND?",
                23 to "  * BREAKPOINT AT 110",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testForegroundTransparent() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 CALL CLEAR
            110 CALL SCREEN(12)
            120 CALL COLOR(2,1,7)
            130 CALL HCHAR(12,3,42,28)
            140 GOTO 140
            """.trimIndent(), machine
        )
        machine.addProgramLineHookAfter({ line -> line.lineNumber == 140 }, { _ ->
            TestHelperScreen.assertColors({ row, col, charColors ->
                if (row == 12 && col in 3..30) {
                    charColors == TiCharacterColor(TiColor.fromCode(12), TiColor.fromCode(7))
                } else {
                    charColors == TiCharacterColor(TiColor.Black, TiColor.LightYellow)
                }
            }, machine.screen)

            machine.addBreakpoint(140)
        })
        interpreter.interpret("RUN", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                10 to "  " + "*".repeat(28),
                23 to "  * BREAKPOINT AT 140",
                24 to " >"
            ), machine.screen
        )
        TestHelperScreen.assertAllColors(TiCharacterColor(TiColor.Black, TiColor.Cyan), machine.screen)
    }

}