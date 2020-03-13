package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.Breakpoint
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.basic.TiBasicProgramException
import com.github.mmrsic.ti99.basic.expr.toChar
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on pages II-76 through II-79.
 */
class CallCharTest {

    @Test
    fun testCenteredSolidBlock() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 CALL CLEAR
            110 CALL CHAR(33,"FFFFFFFFFFFFFFFF")
            120 CALL COLOR(1,9,6)
            130 CALL VCHAR(12,16,33)
            140 GOTO 140
            """.trimIndent(), machine
        )
        machine.addProgramLineHookAfter({ line -> line.lineNumber == 140 }, {
            TestHelperScreen.assertPatterns({ row, col, actualPattern ->
                actualPattern == if (row == 12 && col == 16) "F".repeat(16) else "0".repeat(16)
            }, machine.screen)
            throw TiBasicProgramException(140, Breakpoint())
        })
        interpreter.interpret("RUN", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                10 to " ".repeat(15) + "!",
                23 to "  * BREAKPOINT AT 140",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testDancingHuman() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 CALL CLEAR
            110 A$="1898FF3D3C3CE404"
            120 B$="1819FFBC3C3C2720"
            130 CALL CHAR(128,A$)
            140 CALL CHAR(129,B$)
            150 CALL COLOR(9,7,12)
            160 CALL VCHAR(12,16,128)
            170 FOR DELAY=1 TO 500
            180 NEXT DELAY
            190 CALL VCHAR(12,16,129)
            200 FOR DELAY=1 TO 500
            210 NEXT DELAY
            220 GOTO 140
            230 END
            """.trimIndent(), machine
        )
        machine.addProgramLineHookAfterLine(160) {
            TestHelperScreen.assertPatterns({ row, col, actualPattern ->
                actualPattern == if (row == 12 && col == 16) "1898FF3D3C3CE404" else "0".repeat(16)
            }, machine.screen)
        }
        machine.addProgramLineHookAfterLine(220) {
            TestHelperScreen.assertPatterns({ row, col, actualPattern ->
                actualPattern == if (row == 12 && col == 16) "1819FFBC3C3C2720" else "0".repeat(16)
            }, machine.screen)
        }
        var numCalls = 0
        machine.addProgramLineHookAfter({ line -> line.lineNumber == 220 }, {
            numCalls++
            if (numCalls > 4) {
                throw TiBasicProgramException(220, Breakpoint())
            }
        })
        interpreter.interpret("RUN", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                10 to " ".repeat(15) + toChar(129),
                23 to "  * BREAKPOINT AT 220",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testPrintStatement() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 CALL CLEAR
            110 CALL CHAR(128,"0103070F1F3F7FFF")
            120 PRINT CHR$(128)
            130 END
            """.trimIndent(), machine
        )
        machine.addProgramLineHookAfterLine(120) {
            TestHelperScreen.assertPatterns({ row, col, actualPattern ->
                actualPattern == if (row == 23 && col == 3) "0103070F1F3F7FFF" else "0".repeat(16)
            }, machine.screen)
        }
        interpreter.interpret("RUN", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                20 to "  " + toChar(128),
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
        val actualPatternAfterEnd = machine.screen.patterns.at(20, 3)
        assert(actualPatternAfterEnd == "0103070F1F3F7FFF") { "Wrong pattern: $actualPatternAfterEnd" }
    }

    @Test
    fun testChangeAfterProgramEnd() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 CALL CLEAR
            110 CALL CHAR(128,"FFFFFFFFFFFFFFFF")
            120 CALL CHAR(42,"0F0F0F0F0F0F0F0F")
            130 CALL HCHAR(12,17,42)
            140 CALL HCHAR(14,17,128)
            150 FOR DELAY=1 TO 350
            160 NEXT DELAY
            170 END
            """.trimIndent(), machine
        )
        machine.addProgramLineHookAfterLine(140) {
            TestHelperScreen.assertPatterns({ row, col, actualPattern ->
                actualPattern == when {
                    row == 12 && col == 17 -> "0F".repeat(8)
                    row == 14 && col == 17 -> "F".repeat(16)
                    else -> "0".repeat(16)
                }
            }, machine.screen)
        }
        interpreter.interpret("RUN", machine)

        TestHelperScreen.assertPatterns({ row, col, actualPattern ->
            when {
                row == 9 && col == 17 -> actualPattern == "000028107C102800"
                row == 11 && col == 17 -> actualPattern == "F".repeat(16)
                else -> true
            }
        }, machine.screen)
    }

}