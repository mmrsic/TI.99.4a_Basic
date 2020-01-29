package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on pages II-33 and II-34.
 */
class UnbreakCommandTest {

    @Test
    fun testUnbreakSingleLine() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 A=26.7
            110 C=19.3
            120 PRINT A
            130 PRINT C
            140 END
            BREAK 110,130
            RUN
            UNBREAK 130
            CONTINUE
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                4 to "  TI BASIC READY",
                6 to " >100 A=26.7",
                7 to " >110 C=19.3",
                8 to " >120 PRINT A",
                9 to " >130 PRINT C",
                10 to " >140 END",
                11 to " >BREAK 110,130",
                13 to " >RUN",
                15 to "  * BREAKPOINT AT 110",
                16 to " >UNBREAK 130",
                18 to " >CONTINUE",
                19 to "   26.7",
                20 to "   19.3",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testUnbreakAllLinesButBreakStatements() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 A=26.7
            110 C=19.3
            120 PRINT A
            130 PRINT C
            140 END
            125 BREAK
            BREAK 100,120,130
            RUN
            UNBREAK
            CONTINUE
            CONTINUE
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                2 to " >100 A=26.7",
                3 to " >110 C=19.3",
                4 to " >120 PRINT A",
                5 to " >130 PRINT C",
                6 to " >140 END",
                7 to " >125 BREAK",
                8 to " >BREAK 100,120,130",
                10 to " >RUN",
                12 to "  * BREAKPOINT AT 100",
                13 to " >UNBREAK",
                15 to " >CONTINUE",
                16 to "   26.7",
                18 to "  * BREAKPOINT AT 125",
                19 to " >CONTINUE",
                20 to "   19.3",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

}