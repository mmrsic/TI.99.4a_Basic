package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on page II-64.
 */
class RestoreStatementTest {

    @Test
    fun testTwoDataStatementsWhereBothAreNotReadUntilEnd() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 FOR I=1 TO 2
            110 FOR J=1 TO 4
            120 READ A
            130 PRINT A;
            140 NEXT J
            150 RESTORE 180
            160 NEXT I
            170 DATA 12,33,41,26,42,50
            180 DATA 10,20,30,40,50
            190 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                7 to "  TI BASIC READY",
                9 to " >100 FOR I=1 TO 2",
                10 to " >110 FOR J=1 TO 4",
                11 to " >120 READ A",
                12 to " >130 PRINT A;",
                13 to " >140 NEXT J",
                14 to " >150 RESTORE 180",
                15 to " >160 NEXT I",
                16 to " >170 DATA 12,33,41,26,42,50",
                17 to " >180 DATA 10,20,30,40,50",
                18 to " >190 END",
                19 to " >RUN",
                20 to "   12  33  41  26  10  20  30",
                21 to "   40",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testWithoutLineNumberAfterEveryRead() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 FOR I=1 TO 5
            110 READ X
            120 RESTORE
            130 PRINT X;
            140 NEXT I
            150 DATA 10,20,30
            160 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                11 to "  TI BASIC READY",
                13 to " >100 FOR I=1 TO 5",
                14 to " >110 READ X",
                15 to " >120 RESTORE",
                16 to " >130 PRINT X;",
                17 to " >140 NEXT I",
                18 to " >150 DATA 10,20,30",
                19 to " >160 END",
                20 to " >RUN",
                21 to "   10  10  10  10  10",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testNextDataStatementWithGreaterLineNumber() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 READ A,B
            110 RESTORE 130
            120 PRINT A;B
            130 READ C,D
            140 PRINT C;D
            150 DATA 26.9,34.67
            160 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                9 to "  TI BASIC READY",
                11 to " >100 READ A,B",
                12 to " >110 RESTORE 130",
                13 to " >120 PRINT A;B",
                14 to " >130 READ C,D",
                15 to " >140 PRINT C;D",
                16 to " >150 DATA 26.9,34.67",
                17 to " >160 END",
                18 to " >RUN",
                19 to "   26.9  34.67",
                20 to "   26.9  34.67",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testNonExistingLineNumberButNextDataStatementWitGreaterLineNumber() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 READ A,B
            110 RESTORE 145
            120 PRINT A;B
            130 READ C,D
            140 PRINT C;D
            150 DATA 26.9,34.67
            160 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                9 to "  TI BASIC READY",
                11 to " >100 READ A,B",
                12 to " >110 RESTORE 145",
                13 to " >120 PRINT A;B",
                14 to " >130 READ C,D",
                15 to " >140 PRINT C;D",
                16 to " >150 DATA 26.9,34.67",
                17 to " >160 END",
                18 to " >RUN",
                19 to "   26.9  34.67",
                20 to "   26.9  34.67",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testNonExistingLineNumberAndNoNextDataStatementWitGreaterLineNumber() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 READ A,B
            110 RESTORE 155
            120 PRINT A;B
            130 READ C,D
            140 PRINT C;D
            150 DATA 26.9,34.67
            160 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                10 to "  TI BASIC READY",
                12 to " >100 READ A,B",
                13 to " >110 RESTORE 155",
                14 to " >120 PRINT A;B",
                15 to " >130 READ C,D",
                16 to " >140 PRINT C;D",
                17 to " >150 DATA 26.9,34.67",
                18 to " >160 END",
                19 to " >RUN",
                20 to "   26.9  34.67",
                22 to "  * DATA ERROR IN 130",
                24 to " >"
            ), machine.screen
        )
    }
}