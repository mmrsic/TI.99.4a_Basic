package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases found in User Reference Guide in section Numeric Expressions on pages II-12 and II-13.
 */
class NumericExpressionsTest {

    @Test
    fun testPlusMinusMultiplicationDivision() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpret("100 A=6", machine)
        interpreter.interpret("110 B=4", machine)
        interpreter.interpret("120 C=20", machine)
        interpreter.interpret("130 D=2", machine)
        interpreter.interpret("140 PRINT A*B/2", machine)
        interpreter.interpret("150 PRINT C-D*3+6", machine)
        interpreter.interpret("160 END", machine)
        interpreter.interpret("RUN", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                9 to "  TI BASIC READY",
                11 to " >100 A=6",
                12 to " >110 B=4",
                13 to " >120 C=20",
                14 to " >130 D=2",
                15 to " >140 PRINT A*B/2",
                16 to " >150 PRINT C-D*3+6",
                17 to " >160 END",
                18 to " >RUN",
                19 to "   12",
                20 to "   20",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testThreePlusNegativeOne() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpret("PRINT 3+-1", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                19 to "  TI BASIC READY",
                21 to " >PRINT 3+-1",
                22 to "   2",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testTwoMultipliedWithNegativeThree() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpret("PRINT 2*-3", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                19 to "  TI BASIC READY",
                21 to " >PRINT 2*-3",
                22 to "  -6",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testSixDividedByNegativeThree() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpret("PRINT 6/-3", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                19 to "  TI BASIC READY",
                21 to " >PRINT 6/-3",
                22 to "  -2",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testParenthesisExponentiation() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpret("100 A=2", machine)
        interpreter.interpret("110 B=3", machine)
        interpreter.interpret("120 C=4", machine)
        interpreter.interpret("130 PRINT A*(B+2)", machine)
        interpreter.interpret("140 PRINT B^A-4", machine)
        interpreter.interpret("150 PRINT -C^A;(-C)^A", machine)
        interpreter.interpret("160 PRINT 10-B*C/6", machine)
        interpreter.interpret("170 END", machine)
        interpreter.interpret("RUN", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                6 to "  TI BASIC READY",
                8 to " >100 A=2",
                9 to " >110 B=3",
                10 to " >120 C=4",
                11 to " >130 PRINT A*(B+2)",
                12 to " >140 PRINT B^A-4",
                13 to " >150 PRINT -C^A;(-C)^A",
                14 to " >160 PRINT 10-B*C/6",
                15 to " >170 END",
                16 to " >RUN",
                17 to "   10",
                18 to "   5",
                19 to "  -16  16",
                20 to "   8",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testZeroExponentiationZeroGivesOne() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpret("PRINT 0^0", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                19 to "  TI BASIC READY",
                21 to " >PRINT 0^0",
                22 to "   1",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testProgramRunWithNumericUnderflowAndOverflow() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 PRINT 1E-200
            110 PRINT 24+1E-139
            120 PRINT 1E171
            130 PRINT (1E60*1E76)/1E50
            140 END
            RUN
        """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                3 to "  TI BASIC READY",
                5 to " >100 PRINT 1E-200",
                6 to " >110 PRINT 24+1E-139",
                7 to " >120 PRINT 1E171",
                8 to " >130 PRINT (1E60*1E76)/1E50",
                9 to " >140 END",
                10 to " >RUN",
                11 to "   0",
                12 to "   24",
                14 to "  * WARNING:",
                15 to "    NUMBER TOO BIG IN 120",
                16 to "   9.99999E+**",
                18 to "  * WARNING:",
                19 to "    NUMBER TOO BIG IN 130",
                20 to "   1.E+78",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testDivisionByZero_zeroRaisedToNegativePower_negativeNumberRaisedToNonIntegralPower() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 PRINT -22/0
            110 PRINT 0^-2
            120 PRINT (-3)^1.2
            130 END
            RUN
        """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                6 to "  TI BASIC READY",
                8 to " >100 PRINT -22/0",
                9 to " >110 PRINT 0^-2",
                10 to " >120 PRINT (-3)^1.2",
                11 to " >130 END",
                12 to " >RUN",
                14 to "  * WARNING:",
                15 to "    NUMBER TOO BIG IN 100",
                16 to "  -9.99999E+**",
                18 to "  * WARNING:",
                19 to "    NUMBER TOO BIG IN 110",
                20 to "   9.99999E+**",
                22 to "  * BAD VALUE IN 120",
                24 to " >"
            ), machine.screen
        )
    }

}