package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples from User's Reference Guide on page II-33 and II-34.
 */
class UnbreakStatementTest {

    @Test
    fun testUnbreakFollowingLine() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 A=26.7
            110 C=19.3
            120 PRINT A
            130 PRINT C
            140 END
            BREAK 130
            125 UNBREAK 130
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                8 to "  TI BASIC READY",
                10 to " >100 A=26.7",
                11 to " >110 C=19.3",
                12 to " >120 PRINT A",
                13 to " >130 PRINT C",
                14 to " >140 END",
                15 to " >BREAK 130",
                17 to " >125 UNBREAK 130",
                18 to " >RUN",
                19 to "   26.7",
                20 to "   19.3",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

}