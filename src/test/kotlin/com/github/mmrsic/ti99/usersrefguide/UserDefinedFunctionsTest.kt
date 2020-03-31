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

}