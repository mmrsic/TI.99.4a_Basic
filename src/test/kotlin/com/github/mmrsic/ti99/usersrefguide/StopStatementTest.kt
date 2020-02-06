package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on page II-48.
 */
class StopStatementTest {

    @Test
    fun testLastStatementInProgram() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 A=5
            110 B$="TEXAS INSTRUMENTS"
            120 PRINT B$;A
            130 STOP
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                13 to "  TI BASIC READY",
                15 to " >100 A=5",
                16 to " >110 B$=\"TEXAS INSTRUMENTS\"",
                17 to " >120 PRINT B$;A",
                18 to " >130 STOP",
                19 to " >RUN",
                20 to "  TEXAS INSTRUMENTS 5",
                22 to "  ** DONE **",
                24 to " >"
            ),
            machine.screen
        )

    }

}