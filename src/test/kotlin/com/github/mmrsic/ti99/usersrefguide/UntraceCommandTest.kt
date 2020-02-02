package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on page II-37.
 */
class UntraceCommandTest {

    @Test
    fun testEntireProgram() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 FOR I=1 TO 2
            110 PRINT I
            120 NEXT I
            130 END
            TRACE
            RUN
            UNTRACE
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                2 to "  TI BASIC READY",
                4 to " >100 FOR I=1 TO 2",
                5 to " >110 PRINT I",
                6 to " >120 NEXT I",
                7 to " >130 END",
                8 to " >TRACE",
                10 to " >RUN",
                11 to "  <100><110> 1",
                12 to "  <120><110> 2",
                13 to "  <120><130>",
                14 to "  ** DONE **",
                16 to " >UNTRACE",
                18 to " >RUN",
                19 to "   1",
                20 to "   2",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

}