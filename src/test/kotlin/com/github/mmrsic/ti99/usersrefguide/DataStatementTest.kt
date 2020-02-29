package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on page II-63.
 */
class DataStatementTest {

    @Test
    fun testNumbersInForLoop() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 FOR I=1 TO 5
            110 READ A,B
            120 PRINT A;B
            130 NEXT I
            140 DATA 2,4,6,7,8
            150 DATA 1,2,3,4,5
            160 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                6 to "  TI BASIC READY",
                8 to " >100 FOR I=1 TO 5",
                9 to " >110 READ A,B",
                10 to " >120 PRINT A;B",
                11 to " >130 NEXT I",
                12 to " >140 DATA 2,4,6,7,8",
                13 to " >150 DATA 1,2,3,4,5",
                14 to " >160 END",
                15 to " >RUN",
                16 to "   2  4",
                17 to "   6  7",
                18 to "   8  1",
                19 to "   2  3",
                20 to "   4  5",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testVariableType() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
                100 READ A$,B$,C,D
                110 PRINT A$:B$:C:D
                120 DATA HELLO,"JONES, MARY",28,3.1416
                130 END
                RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                9 to "  TI BASIC READY",
                11 to " >100 READ A$,B$,C,D",
                12 to " >110 PRINT A$:B$:C:D",
                13 to " >120 DATA HELLO,\"JONES, MARY\"",
                14 to "  ,28,3.1416",
                15 to " >130 END",
                16 to " >RUN",
                17 to "  HELLO",
                18 to "  JONES, MARY",
                19 to "   28",
                20 to "   3.1416",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testEmptyUnquotedString() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
                100 READ A$,B$,C
                110 DATA HI,,2
                120 PRINT "A$ IS ";A$
                130 PRINT "B$ IS ";B$
                140 PRINT "C IS ";C
                150 END
                RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                9 to "  TI BASIC READY",
                11 to " >100 READ A$,B$,C",
                12 to " >110 DATA HI,,2",
                13 to " >120 PRINT \"A$ IS \";A$",
                14 to " >130 PRINT \"B$ IS \";B$",
                15 to " >140 PRINT \"C IS \";C",
                16 to " >150 END",
                17 to " >RUN",
                18 to "  A$ IS HI",
                19 to "  B$ IS",
                20 to "  C IS  2",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

}