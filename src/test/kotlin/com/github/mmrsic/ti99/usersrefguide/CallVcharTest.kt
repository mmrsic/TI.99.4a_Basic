package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.Breakpoint
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.basic.TiBasicProgramException
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide in page II-83.
 */
class CallVcharTest {

    @Test
    fun testCommand() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            CALL CLEAR
            CALL VCHAR(2,10,86,13)
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                1 to " ".repeat(9) + "V",
                2 to " ".repeat(9) + "V",
                3 to " ".repeat(9) + "V",
                4 to " ".repeat(9) + "V",
                5 to " ".repeat(9) + "V",
                6 to " ".repeat(9) + "V",
                7 to " ".repeat(9) + "V",
                8 to " ".repeat(9) + "V",
                9 to " ".repeat(9) + "V",
                10 to " ".repeat(9) + "V",
                11 to " ".repeat(9) + "V",
                12 to " ".repeat(9) + "V",
                13 to " ".repeat(9) + "V",
                22 to " >CALL VCHAR(2,10,86,13)",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testStatement() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 CALL CLEAR
            110 FOR I=13 TO 18
            120 CALL VCHAR(9,I,36,6)
            130 NEXT I
            140 GOTO 140
            """.trimIndent(), machine
        )
        machine.addProgramLineHookAfterLine(140) {
            throw TiBasicProgramException(140, Breakpoint())
        }
        interpreter.interpret("RUN", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                7 to " ".repeat(12) + "$".repeat(6),
                8 to " ".repeat(12) + "$".repeat(6),
                9 to " ".repeat(12) + "$".repeat(6),
                10 to " ".repeat(12) + "$".repeat(6),
                11 to " ".repeat(12) + "$".repeat(6),
                12 to " ".repeat(12) + "$".repeat(6),
                23 to "  * BREAKPOINT AT 140",
                24 to " >"
            ), machine.screen
        )
    }

}