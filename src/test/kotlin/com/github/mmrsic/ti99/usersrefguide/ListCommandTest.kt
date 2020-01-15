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
        interpreter.interpretAll(
            """
                100 A=279.3
                120 PRINT A;B
                110 B=-456.8
                130 END
                LIST
            """.trimIndent()
            , machine
        )

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
}