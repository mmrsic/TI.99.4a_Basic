package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples of User's Reference Guide on pages II-35 and II-36.
 */
class ContinueCommandTest {

    @Test
    fun testSimpleCases() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 A=9.6
            110 PRINT A
            120 END
            BREAK 110
            RUN
            CON
            BREAK 110
            RUN
            100 A=10.1
            CONTINUE
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                2 to " >100 A=9.6",
                3 to " >110 PRINT A",
                4 to " >120 END",
                5 to " >BREAK 110",
                7 to " >RUN",
                9 to "  * BREAKPOINT AT 110",
                10 to " >CON",
                11 to "   9.6",
                13 to "  ** DONE **",
                15 to " >BREAK 110",
                17 to " >RUN",
                19 to "  * BREAKPOINT AT 110",
                20 to " >100 A=10.1",
                21 to " >CONTINUE",
                22 to "  * CAN'T CONTINUE",
                24 to " >"
            ), machine.screen
        )
    }

}