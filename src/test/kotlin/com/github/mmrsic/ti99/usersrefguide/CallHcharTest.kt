package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.Breakpoint
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.basic.TiBasicProgramException
import com.github.mmrsic.ti99.hw.TiBasicModule
import com.github.mmrsic.ti99.hw.TiCharacterColor
import com.github.mmrsic.ti99.hw.TiColor
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on pages II-80 through II-82.
 */
class CallHcharTest {

    @Test
    fun testCommand() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            CALL CLEAR
            CALL HCHAR(10,1,72,50)
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                9 to "H".repeat(32),
                10 to "H".repeat(18),
                22 to " >CALL HCHAR(10,1,72,50)",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testColoredBarAnimation() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 CALL CLEAR
            110 FOR S=2 TO 16
            120 CALL COLOR(S,S,S)
            130 NEXT S
            140 CHR=40
            150 FOR X=8 TO 22
            160 CALL VCHAR(4,X,CHR,15)
            170 CALL HCHAR(X-4,8,CHR,15)
            180 CHR=CHR+8
            190 NEXT X
            200 GOTO 140
            """.trimIndent(), machine
        )
        machine.addProgramLineHookAfterLine(200) {
//            machine.screen.colors.forEachCellDo { r, c, charcols-> println("$r:$c = $charcols")}
            TestHelperScreen.assertColors({ row, col, actualColors ->
                actualColors == when {
                    row < 4 || row > 18 || col < 8 || col > 22 -> TiCharacterColor(2, 4)
                    row == 4 && col == 8 -> TiCharacterColor(2, 2)
                    row <= 5 && col <= 9 -> TiCharacterColor(3, 3)
                    row <= 6 && col <= 10 -> TiCharacterColor(4, 4)
                    row <= 7 && col <= 11 -> TiCharacterColor(5, 5)
                    row <= 8 && col <= 12 -> TiCharacterColor(6, 6)
                    row <= 9 && col <= 13 -> TiCharacterColor(7, 7)
                    row <= 10 && col <= 14 -> TiCharacterColor(8, 8)
                    row <= 11 && col <= 15 -> TiCharacterColor(9, 9)
                    row <= 12 && col <= 16 -> TiCharacterColor(10, 10)
                    row <= 13 && col <= 17 -> TiCharacterColor(11, 11)
                    row <= 14 && col <= 18 -> TiCharacterColor(12, 12)
                    row <= 15 && col <= 19 -> TiCharacterColor(13, 13)
                    row <= 16 && col <= 20 -> TiCharacterColor(14, 14)
                    row <= 17 && col <= 21 -> TiCharacterColor(15, 15)
                    row <= 18 && col <= 22 -> TiCharacterColor(16, 16)
                    else -> throw IllegalArgumentException("Missing expected color at row=$row, column=$col: $actualColors")
                }
            }, machine.screen)

            throw TiBasicProgramException(200, Breakpoint())
        }
        interpreter.interpret("RUN", machine)

        TestHelperScreen.assertAllColorsEqual(TiCharacterColor(TiColor.Black, TiColor.Cyan), machine.screen)
    }

}