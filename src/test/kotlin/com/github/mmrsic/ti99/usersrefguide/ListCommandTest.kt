package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples in section Commands of User Reference Guide on page II-21.
 */
class ListCommandTest {

    @Test
    fun testListEntireProgramWhenLinesAreEnteredOutOfOrder() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        enterExampleProgram(machine, interpreter)
        interpreter.interpret("LIST", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                13 to "  TI BASIC READY",
                15 to " >100 A=279.3",
                16 to " >120 PRINT A;B",
                17 to " >110 B=-456.8",
                18 to " >130 END",
                19 to " >LIST",
                20 to "  100 A=279.3",
                21 to "  110 B=-456.8",
                22 to "  120 PRINT A;B",
                23 to "  130 END",
                24 to " >"
            ),
            machine.screen
        )
    }

    @Test
    fun testListSingleProgramLine() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        enterExampleProgram(machine, interpreter)
        interpreter.interpret("LIST 110", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                16 to "  TI BASIC READY",
                18 to " >100 A=279.3",
                19 to " >120 PRINT A;B",
                20 to " >110 B=-456.8",
                21 to " >130 END",
                22 to " >LIST 110",
                23 to "  110 B=-456.8",
                24 to " >"
            ),
            machine.screen
        )
    }

    @Test
    fun testListProgramUntilGivenLineNumber() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        enterExampleProgram(machine, interpreter)
        interpreter.interpret("LIST -110", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                15 to "  TI BASIC READY",
                17 to " >100 A=279.3",
                18 to " >120 PRINT A;B",
                19 to " >110 B=-456.8",
                20 to " >130 END",
                21 to " >LIST -110",
                22 to "  100 A=279.3",
                23 to "  110 B=-456.8",
                24 to " >"
            ),
            machine.screen
        )
    }

    @Test
    fun testListProgramFromGivenLineNumber() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        enterExampleProgram(machine, interpreter)
        interpreter.interpret("LIST 120-", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                15 to "  TI BASIC READY",
                17 to " >100 A=279.3",
                18 to " >120 PRINT A;B",
                19 to " >110 B=-456.8",
                20 to " >130 END",
                21 to " >LIST 120-",
                22 to "  120 PRINT A;B",
                23 to "  130 END",
                24 to " >"
            ),
            machine.screen
        )
    }


    // HELPERS //

    private fun enterExampleProgram(machine: TiBasicModule, interpreter: TiBasicCommandLineInterpreter) {
        interpreter.interpretAll(
            """
                    100 A=279.3
                    120 PRINT A;B
                    110 B=-456.8
                    130 END
                """.trimIndent()
            , machine
        )
    }
}