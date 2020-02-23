package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on pages II-61 and II-62.
 */
class ReadStatementTest {

    @Test
    fun testTwoNumericVariables() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
                100 FOR I=1 TO 3
                110 READ X,Y
                120 PRINT X;Y
                130 NEXT I
                140 DATA 22,15,36,52,48,96.5
                150 END
                RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                8 to "  TI BASIC READY",
                10 to " >100 FOR I=1 TO 3",
                11 to " >110 READ X,Y",
                12 to " >120 PRINT X;Y",
                13 to " >130 NEXT I",
                14 to " >140 DATA 22,15,36,52,48,96.5",
                16 to " >150 END",
                17 to " >RUN",
                18 to "   22  15",
                19 to "   36  52",
                20 to "   48  96.5",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

}