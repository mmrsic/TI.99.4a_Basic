package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on pages from II-65 to II-69.
 */
class PrintStatementTest {

    @Test
    fun testNumericAndStringVariablesAndCommaIncludingStringConstant() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 A=10
            110 B=20
            120 STRING$="TI COMPUTER"
            130 PRINT A;B:STRING$
            140 PRINT "HELLO, FRIEND"
            150 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                9 to "  TI BASIC READY",
                11 to " >100 A=10",
                12 to " >110 B=20",
                13 to " >120 STRING$=\"TI COMPUTER\"",
                14 to " >130 PRINT A;B:STRING$",
                15 to " >140 PRINT \"HELLO, FRIEND\"",
                16 to " >150 END",
                17 to " >RUN",
                18 to "   10  20",
                19 to "  TI COMPUTER",
                20 to "  HELLO, FRIEND",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testStringConcatenation() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 N$="JOAN"
            110 M$="HI"
            120 PRINT M$;N$
            130 PRINT M$&" "&N$
            140 PRINT "HELLO ";N$
            150 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                9 to "  TI BASIC READY",
                11 to " >100 N$=\"JOAN\"",
                12 to " >110 M$=\"HI\"",
                13 to " >120 PRINT M$;N$",
                14 to " >130 PRINT M$&\" \"&N$",
                15 to " >140 PRINT \"HELLO \";N$",
                16 to " >150 END",
                17 to " >RUN",
                18 to "  HIJOAN",
                19 to "  HI JOAN",
                20 to "  HELLO JOAN",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testAddition() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 LET A=10.2
            110 B=-30.5
            120 C=16.7
            130 PRINT A;B;C
            140 PRINT A+B
            150 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                10 to "  TI BASIC READY",
                12 to " >100 LET A=10.2",
                13 to " >110 B=-30.5",
                14 to " >120 C=16.7",
                15 to " >130 PRINT A;B;C",
                16 to " >140 PRINT A+B",
                17 to " >150 END",
                18 to " >RUN",
                19 to "   10.2 -30.5  16.7",
                20 to "  -20.3",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testNumbersWithTenOrFewerDigitsInNormalDecimalForm() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            PRINT -10;7.1
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                19 to "  TI BASIC READY",
                21 to " >PRINT -10;7.1",
                22 to "  -10  7.1",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testNumbersWithMoreThanTenDigitsInScientificForm() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            PRINT 93427685127
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                19 to "  TI BASIC READY",
                21 to " >PRINT 93427685127",
                22 to "   9.34277E+10",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testNonIntegerNumbersInNormalOrInScientificForm() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            PRINT 1E-10
            PRINT 1.2E-10
            PRINT .000000000246
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                13 to "  TI BASIC READY",
                15 to " >PRINT 1E-10",
                16 to "   .0000000001",
                18 to " >PRINT 1.2E-10",
                19 to "   1.2E-10",
                21 to " >PRINT .000000000246",
                22 to "   2.46E-10",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testIntegers() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpret("PRINT 15;-3", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                19 to "  TI BASIC READY",
                21 to " >PRINT 15;-3",
                22 to "   15 -3",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testNonIntegers() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpret("PRINT 3.350;-46.1", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                19 to "  TI BASIC READY",
                21 to " >PRINT 3.350;-46.1",
                22 to "   3.35 -46.1",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testNonIntegerWithMoreThanTenDigits() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpret("PRINT 791.123456789", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                19 to "  TI BASIC READY",
                21 to " >PRINT 791.123456789",
                22 to "   791.1234568",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testNumbersWithAbsoluteValueLessThanOne() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpret("PRINT -12.7E-3;0.64", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                19 to "  TI BASIC READY",
                21 to " >PRINT -12.7E-3;0.64",
                22 to "  -.0127  .64",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testMantissaWithUpToSixDigitsAndAlwasOneDigitToTheLeftOfDecimalPoint() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            listOf(
                "PRINT .0000000001978531",
                "PRINT -98.77E21"
            ), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                16 to "  TI BASIC READY",
                18 to " >PRINT .0000000001978531",
                19 to "   1.97853E-10",
                21 to " >PRINT -98.77E21",
                22 to "  -9.877E+22",
                24 to " >"
            ), machine.screen
        )
    }

}