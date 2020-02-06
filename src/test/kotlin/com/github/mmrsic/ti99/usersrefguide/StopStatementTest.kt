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

    @Test
    fun testInFrontOfSubProgram() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 CALL CLEAR
            110 FOR I=1 TO 15
            120 CALL HCHAR(1,1,42,768)
            130 GOSUB 160
            140 NEXT I
            150 STOP
            160 F=I
            170 B=I+1
            180 CALL COLOR(2,F,B)
            190 RETURN
            200 END
            RUN
            """.trimIndent(), machine
        )

        // The color is not tested here as only the STOP statement is of interest
        TestHelperScreen.assertPrintContents(
            mapOf(
                1 to "********************************",
                2 to "********************************",
                3 to "********************************",
                4 to "********************************",
                5 to "********************************",
                6 to "********************************",
                7 to "********************************",
                8 to "********************************",
                9 to "********************************",
                10 to "********************************",
                11 to "********************************",
                12 to "********************************",
                13 to "********************************",
                14 to "********************************",
                15 to "********************************",
                16 to "********************************",
                17 to "********************************",
                18 to "********************************",
                19 to "********************************",
                20 to "********************************",
                21 to "********************************",
                22 to "  ** DONE **",
                24 to " >"
            ),
            machine.screen
        )
    }

}