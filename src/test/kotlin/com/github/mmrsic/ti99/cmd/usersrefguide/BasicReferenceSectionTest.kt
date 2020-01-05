package com.github.mmrsic.ti99.cmd.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Ignore
import org.junit.Test

class BasicReferenceSectionTest {

    @Test
    fun test_II_4_generalInformation() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)

        interpreter.interpret("NEW", machine)

        TestHelperScreen.assertPrintContents(mapOf(22 to "  TI BASIC READY", 24 to " >"), machine.screen)
        TestHelperScreen.assertCursorAt(24, 3, machine.screen)

        interpreter.interpret("10 A=2", machine)
        interpreter.interpret("RUN", machine)
        interpreter.interpret("PRINT A", machine)
        interpreter.interpret("20 B=3", machine)
        interpreter.interpret("PRINT A", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                10 to "  TI BASIC READY",
                12 to " >10 A=2",
                13 to " >RUN",
                15 to "  ** DONE **",
                17 to " >PRINT A",
                18 to "   2",
                20 to " >20 B=3",
                21 to " >PRINT A",
                22 to "   0",
                24 to " >"
            ), machine.screen
        )
        TestHelperScreen.assertCursorAt(24, 3, machine.screen)
    }

    @Test
    fun test_II_8_lineNumbers() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpret("100 A=27.9", machine)
        interpreter.interpret("110 B=31.8", machine)
        interpreter.interpret("120 PRINT A;B", machine)
        interpreter.interpret("130 END", machine)
        interpreter.interpret("RUN", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                13 to "  TI BASIC READY",
                15 to " >100 A=27.9",
                16 to " >110 B=31.8",
                17 to " >120 PRINT A;B",
                18 to " >130 END",
                19 to " >RUN",
                20 to "   27.9  31.8",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun test_II_8_lineNumberToLow() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpret("0 A=2", machine)
        TestHelperScreen.assertPrintContents(
            mapOf(
                18 to "  TI BASIC READY",
                20 to " >0 A=2",
                22 to "  * BAD LINE NUMBER",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun test_II_8_lineNumberTooHigh() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpret("33000 C=4", machine)
        TestHelperScreen.assertPrintContents(
            mapOf(
                18 to "  TI BASIC READY",
                20 to " >33000 C=4",
                22 to "  * BAD LINE NUMBER",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun test_II_9_numberConstants() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpret("PRINT 1.2", machine)
        interpreter.interpret("PRINT -3", machine)
        interpreter.interpret("PRINT 0", machine)
        TestHelperScreen.assertPrintContents(
            mapOf(
                13 to "  TI BASIC READY",
                15 to " >PRINT 1.2",
                16 to "   1.2",
                18 to " >PRINT -3",
                19 to "  -3",
                21 to " >PRINT 0",
                22 to "   0",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun test_II_9_scientificNotation() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpret("PRINT 3.264E4", machine)
        interpreter.interpret("PRINT -98.77E21", machine)
        interpreter.interpret("PRINT -9E-130", machine)
        interpreter.interpret("PRINT 9E-142", machine)
        interpreter.interpret("PRINT 97E136", machine)
        interpreter.interpret("PRINT -108E144", machine)
        TestHelperScreen.assertPrintContents(
            mapOf(
                1 to "   32640",
                3 to " >PRINT -98.77E21",
                4 to "  -9.877E+22",
                6 to " >PRINT -9E-130",
                7 to "   0",
                9 to " >PRINT 9E-142",
                10 to "   0",
                12 to " >PRINT 97E136",
                14 to "  * WARNING:",
                15 to "    NUMBER TOO BIG",
                16 to "   9.99999E+**",
                18 to " >PRINT -108E144",
                20 to "  * WARNING:",
                21 to "    NUMBER TOO BIG",
                22 to "  -9.99999E+**",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun test_II_10_stringConstants() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            listOf(
                "100 PRINT \"HI!\"",
                "110 PRINT \"THIS IS A STRING CONSTANT.\"",
                "120 PRINT \"ALL CHARACTERS (+-*/ @,) MAY BE USED.\"",
                "130 END",
                "RUN"
            ), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                8 to "  TI BASIC READY",
                10 to " >100 PRINT \"HI!\"",
                11 to " >110 PRINT \"THIS IS A STRING",
                12 to "  CONSTANT.\"",
                13 to " >120 PRINT \"ALL CHARACTERS (+",
                14 to "  -*/ @,) MAY BE USED.\"",
                15 to " >130 END",
                16 to " >RUN",
                17 to "  HI!",
                18 to "  THIS IS A STRING CONSTANT.",
                19 to "  ALL CHARACTERS (+-*/ @,) MAY",
                20 to "   BE USED.",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun test_II_10_stringConstantsWithQuotes() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            listOf(
                "100 PRINT \"TO PRINT \"\"QUOTE MARKS\"\" YOU MUST USE DOUBLE QUOTES.\"",
                "110 PRINT",
                "120 PRINT \"TOM SAID, \"\"HI, MARY!\"\"\"",
                "130 END",
                "RUN"
            ), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                7 to "  TI BASIC READY",
                9 to " >100 PRINT \"TO PRINT \"\"QUOTE",
                10 to "  MARKS\"\" YOU MUST USE DOUBLE",
                11 to "  QUOTES.\"",
                12 to " >110 PRINT",
                13 to " >120 PRINT \"TOM SAID, \"\"HI, M",
                14 to "  ARY!\"\"\"",
                15 to " >130 END",
                16 to " >RUN",
                17 to "  TO PRINT \"QUOTE MARKS\" YOU M",
                18 to "  UST USE DOUBLE QUOTES.",
                20 to "  TOM SAID, \"HI, MARY!\"",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun test_II_11_badNameWhenVariableTooLong() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpret("110 ABCDEFGHIJKLMNOPQ=3", machine)
        interpreter.interpret("ABCDEFGHIJKLMNOPQ=13", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                16 to "  TI BASIC READY",
                18 to " >110 ABCDEFGHIJKLMNOPQ=3",
                19 to "  * BAD NAME",
                21 to " >ABCDEFGHIJKLMNOPQ=13",
                22 to "  * BAD NAME",
                24 to " >"
            ), machine.screen
        )
    }

    @Ignore("Not yet implemented")
    @Test
    fun test_II_11_listIsNotAllowedAsNumericVariable_noProgram() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpret("LIST=1", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                18 to "  TI BASIC READY",
                20 to " >LIST=1",
                22 to "  * CAN'T DO THAT",
                24 to " >"
            ), machine.screen
        )
    }

    @Ignore("Not yet implemented")
    @Test
    fun test_II_11_listIsNotAllowedAsNumericVariable_programPresent() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpret("10 A=0", machine)
        interpreter.interpret("LIST=1", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                18 to "  TI BASIC READY",
                20 to " >10 REM TEST LIST",
                21 to " >LIST=1",
                22 to "  * INCORRECT STATEMENT",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun test_II_11_listIsAllowedAsStringVariable() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpret("LIST$=\"1\"", machine)
        interpreter.interpret("PRINT LIST$", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                17 to "  TI BASIC READY",
                19 to " >LIST$=\"1\"",
                21 to " >PRINT LIST$",
                22 to "  1",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun test_II_11_stringsAreLimitedTo255Characters() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpret("TEN$=\"1234567890\"", machine)
        interpreter.interpret("FIFTY$=TEN$&TEN$&TEN$&TEN$&TEN$", machine)
        interpreter.interpret("THREEHUNDRED$=FIFTY$&FIFTY$&FIFTY$&FIFTY$&FIFTY$&FIFTY$", machine)
        interpreter.interpret("PRINT FIFTY$", machine)
        interpreter.interpret("PRINT THREEHUNDRED$", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                2 to " >FIFTY$=TEN$&TEN$&TEN$&TEN$&T",
                3 to "  EN$",
                5 to " >THREEHUNDRED$=FIFTY$&FIFTY$&",
                6 to "  FIFTY$&FIFTY$&FIFTY$&FIFTY$",
                8 to " >PRINT FIFTY$",
                9 to "  1234567890123456789012345678",
                10 to "  9012345678901234567890",
                12 to " >PRINT THREEHUNDRED$",
                13 to "  1234567890123456789012345678",
                14 to "  9012345678901234567890123456",
                15 to "  7890123456789012345678901234",
                16 to "  5678901234567890123456789012",
                17 to "  3456789012345678901234567890",
                18 to "  1234567890123456789012345678",
                19 to "  9012345678901234567890123456",
                20 to "  7890123456789012345678901234",
                21 to "  5678901234567890123456789012",
                22 to "  345",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun test_II_12_numericExpressionsPlusMinusMultiplicationDivision() {
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
    fun test_II_12_threePlusNegativeOne() {
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
    fun test_II_12_twoMultipliedWithNegativeThree() {
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
    fun test_II_12_sixDividedByNegativeThree() {
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
    fun test_II_13_numericExpressionsParanthesisExponentiation() {
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

}