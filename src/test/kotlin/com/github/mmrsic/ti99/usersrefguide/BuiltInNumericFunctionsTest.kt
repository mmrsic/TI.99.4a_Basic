package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples from User's Reference Guide on pages II-92 through II-98.
 */
class BuiltInNumericFunctionsTest {

    @Test
    fun testAbsoluteValue() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 A=-27.36
            110 B=9.7
            120 PRINT ABS(A);ABS(B)
            130 PRINT ABS(3.8);ABS(-4.5)
            140 PRINT ABS(-3*2)
            150 PRINT ABS(A*(B-3.2))
            160 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                6 to "  TI BASIC READY",
                8 to " >100 A=-27.36",
                9 to " >110 B=9.7",
                10 to " >120 PRINT ABS(A);ABS(B)",
                11 to " >130 PRINT ABS(3.8);ABS(-4.5)",
                13 to " >140 PRINT ABS(-3*2)",
                14 to " >150 PRINT ABS(A*(B-3.2))",
                15 to " >160 END",
                16 to " >RUN",
                17 to "   27.36  9.7",
                18 to "   3.8  4.5",
                19 to "   6",
                20 to "   177.84",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testArcTangent() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 PRINT ATN(.44)
            110 PRINT ATN(1E127)
            120 PRINT ATN(1E-129);ATN(0)
            130 PRINT ATN(.3)*57.295779513079
            140 PRINT ATN(.3)*(180/(4*ATN(1)))
            150 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                4 to "  TI BASIC READY",
                6 to " >100 PRINT ATN(.44)",
                7 to " >110 PRINT ATN(1E127)",
                8 to " >120 PRINT ATN(1E-129);ATN(0)",
                10 to " >130 PRINT ATN(.3)*57.2957795",
                11 to "  13079",
                12 to " >140 PRINT ATN(.3)*(180/(4*AT",
                13 to "  N(1)))",
                14 to " >150 END",
                15 to " >RUN",
                16 to "   .4145068746",
                17 to "   1.570796327",
                18 to "   0  0",
                19 to "   16.69924423",
                20 to "   16.69924423",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testCosine() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 A=1.047197551196
            110 B=60
            120 C=.0174532925994
            130 PRINT COS(A);COS(B*C)
            140 PRINT COS(B*(4*ATN(1))/180)
            150 END
            RUN
            PRINT COS(2.2E11)
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                5 to "  TI BASIC READY",
                7 to " >100 A=1.047197551196",
                8 to " >110 B=60",
                9 to " >120 C=.0174532925994",
                10 to " >130 PRINT COS(A);COS(B*C)",
                11 to " >140 PRINT COS(B*(4*ATN(1))/1",
                12 to "  80)",
                13 to " >150 END",
                14 to " >RUN",
                15 to "   .5  .5",
                16 to "   .5",
                18 to "  ** DONE **",
                20 to " >PRINT COS(2.2E11)",
                22 to "  * BAD ARGUMENT",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testExponential() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 A=3.79
            110 PRINT EXP(A);EXP(9)
            120 PRINT EXP(A*2)
            130 PRINT EXP(LOG(2))
            140 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                10 to "  TI BASIC READY",
                12 to " >100 A=3.79",
                13 to " >110 PRINT EXP(A);EXP(9)",
                14 to " >120 PRINT EXP(A*2)",
                15 to " >130 PRINT EXP(LOG(2))",
                16 to " >140 END",
                17 to " >RUN",
                18 to "   44.25640028  8103.083928",
                19 to "   1958.628965",
                20 to "   2",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testInteger() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 B=.678
            110 A=INT(B*100+.5)/100
            120 PRINT A;INT(B)
            130 PRINT INT(-2.3);INT(2.2)
            140 STOP
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                10 to "  TI BASIC READY",
                12 to " >100 B=.678",
                13 to " >110 A=INT(B*100+.5)/100",
                14 to " >120 PRINT A;INT(B)",
                15 to " >130 PRINT INT(-2.3);INT(2.2)",
                17 to " >140 STOP",
                18 to " >RUN",
                19 to "   .68  0",
                20 to "  -3  2",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testNaturalLogarithm() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 A=3.5
            110 PRINT LOG(A);LOG(A*2)
            120 PRINT LOG(EXP(2))
            130 STOP
            RUN
            PRINT LOG(-3)
            PRINT LOG(3)/LOG(10)
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                5 to "  TI BASIC READY",
                7 to " >100 A=3.5",
                8 to " >110 PRINT LOG(A);LOG(A*2)",
                9 to " >120 PRINT LOG(EXP(2))",
                10 to " >130 STOP",
                11 to " >RUN",
                12 to "   1.252762968  1.945910149",
                13 to "   2.",
                15 to "  ** DONE **",
                17 to " >PRINT LOG(-3)",
                19 to "  * BAD ARGUMENT",
                21 to " >PRINT LOG(3)/LOG(10)",
                22 to "   .4771212547",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testRandomNumber10() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 FOR I=1 TO 5
            110 PRINT INT(10*RND)+1
            120 NEXT I
            130 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                9 to "  TI BASIC READY",
                11 to " >100 FOR I=1 TO 5",
                12 to " >110 PRINT INT(10*RND)+1",
                13 to " >120 NEXT I",
                14 to " >130 END",
                15 to " >RUN",
                16 to "   6",
                17 to "   4",
                18 to "   6",
                19 to "   4",
                20 to "   3",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testRandomNumber20() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 REM RANDOM INTEGERS     BETWEEN 1 AND 20,INCLUSIVE
            110 FOR I=1 TO 5
            120 C=INT(20*RND)+1
            130 PRINT C
            140 NEXT I
            150 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                6 to "  TI BASIC READY",
                8 to " >100 REM RANDOM INTEGERS",
                9 to "  BETWEEN 1 AND 20,INCLUSIVE",
                10 to " >110 FOR I=1 TO 5",
                11 to " >120 C=INT(20*RND)+1",
                12 to " >130 PRINT C",
                13 to " >140 NEXT I",
                14 to " >150 END",
                15 to " >RUN",
                16 to "   11",
                17 to "   8",
                18 to "   11",
                19 to "   8",
                20 to "   6",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testSign() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 A=-23.7
            110 B=6
            120 PRINT SGN(A);SGN(0);SGN(B)
            130 PRINT SGN(-3*3);SGN(B*2)
            140 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                9 to "  TI BASIC READY",
                11 to " >100 A=-23.7",
                12 to " >110 B=6",
                13 to " >120 PRINT SGN(A);SGN(0);SGN(",
                14 to "  B)",
                15 to " >130 PRINT SGN(-3*3);SGN(B*2)",
                17 to " >140 END",
                18 to " >RUN",
                19 to "  -1  0  1",
                20 to "  -1  1",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testSine() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 A=.5235987755982
            110 B=30
            120 C=.01745329251994
            130 PRINT SIN(A);SIN(B*C)
            140 PRINT SIN(B*(4*ATN(1))/180)
            150 END
            RUN
            PRINT SIN(1.9E12)
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                5 to "  TI BASIC READY",
                7 to " >100 A=.5235987755982",
                8 to " >110 B=30",
                9 to " >120 C=.01745329251994",
                10 to " >130 PRINT SIN(A);SIN(B*C)",
                11 to " >140 PRINT SIN(B*(4*ATN(1))/1",
                12 to "  80)",
                13 to " >150 END",
                14 to " >RUN",
                15 to "   .5  .5",
                16 to "   .5",
                18 to "  ** DONE **",
                20 to " >PRINT SIN(1.9E12)",
                22 to "  * BAD ARGUMENT",
                24 to " >"
            ), machine.screen
        )
    }

}