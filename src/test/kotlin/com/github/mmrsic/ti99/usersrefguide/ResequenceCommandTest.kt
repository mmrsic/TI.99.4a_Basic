package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on page II-28.
 */
class ResequenceCommandTest {

    @Test
    fun testResequenceTwentyFiveAndRes() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 A=27.9
            110 B=34.1
            120 PRINT A;B
            130 END
            RESEQUENCE 20,5
            LIST
            RES
            LIST
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                6 to "  TI BASIC READY",
                8 to " >100 A=27.9",
                9 to " >110 B=34.1",
                10 to " >120 PRINT A;B",
                11 to " >130 END",
                12 to " >RESEQUENCE 20,5",
                13 to " >LIST",
                14 to "  20 A=27.9",
                15 to "  25 B=34.1",
                16 to "  30 PRINT A;B",
                17 to "  35 END",
                18 to " >RES",
                19 to " >LIST",
                20 to "  100 A=27.9",
                21 to "  110 B=34.1",
                22 to "  120 PRINT A;B",
                23 to "  130 END",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testResFiftyAndResCommaFive() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 A=27.9
            110 B=34.1
            120 PRINT A;B
            130 END
            RES 50
            LIST
            RES ,5
            LIST
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                6 to "  TI BASIC READY",
                8 to " >100 A=27.9",
                9 to " >110 B=34.1",
                10 to " >120 PRINT A;B",
                11 to " >130 END",
                12 to " >RES 50",
                13 to " >LIST",
                14 to "  50 A=27.9",
                15 to "  60 B=34.1",
                16 to "  70 PRINT A;B",
                17 to "  80 END",
                18 to " >RES ,5",
                19 to " >LIST",
                20 to "  100 A=27.9",
                21 to "  105 B=34.1",
                22 to "  110 PRINT A;B",
                23 to "  115 END",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testRemarkAndGoto() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 REM THE VALUE OF "A" WILL BE PRINTED IN LINE 120
            110 A=A+1
            120 PRINT A
            130 GO TO 110
            RESEQUENCE 10,5
            LIST
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                10 to "  TI BASIC READY",
                12 to " >100 REM THE VALUE OF \"A\" WIL",
                13 to "  L BE PRINTED IN LINE 120",
                14 to " >110 A=A+1",
                15 to " >120 PRINT A",
                16 to " >130 GO TO 110",
                17 to " >RESEQUENCE 10,5",
                18 to " >LIST",
                19 to "  10 REM  THE VALUE OF \"A\" WIL",
                20 to "  L BE PRINTED IN LINE 120",
                21 to "  15 A=A+1",
                22 to "  20 PRINT A",
                23 to "  25 GO TO 15",
                24 to " >"
            ), machine.screen
        )
    }

}