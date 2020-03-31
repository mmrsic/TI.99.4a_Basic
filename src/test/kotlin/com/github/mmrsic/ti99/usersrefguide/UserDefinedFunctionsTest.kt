package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on pages II-105 through II-107.
 */
class UserDefinedFunctionsTest {

    @Test
    fun testPi() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 DEF PI=4*ATN(1)
            110 PRINT COS(60*PI/180)
            120 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                14 to "  TI BASIC READY",
                16 to " >100 DEF PI=4*ATN(1)",
                17 to " >110 PRINT COS(60*PI/180)",
                18 to " >120 END",
                19 to " >RUN",
                20 to "   .5",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testEvaluateCurrentVariableValue() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 REM EVALUATE Y=X*(X-3)
            110 DEF Y=X*(X-3)
            120 PRINT " X  Y"
            130 FOR X=-2 TO 5
            140 PRINT X;Y
            150 NEXT X
            160 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                2 to "  TI BASIC READY",
                4 to " >100 REM EVALUATE Y=X*(X-3)",
                5 to " >110 DEF Y=X*(X-3)",
                6 to " >120 PRINT \" X  Y\"",
                7 to " >130 FOR X=-2 TO 5",
                8 to " >140 PRINT X;Y",
                9 to " >150 NEXT X",
                10 to " >160 END",
                11 to " >RUN",
                12 to "   X  Y",
                13 to "  -2  10",
                14 to "  -1  4",
                15 to "   0  0",
                16 to "   1 -2",
                17 to "   2 -2",
                18 to "   3  0",
                19 to "   4  4",
                20 to "   5  10",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

}