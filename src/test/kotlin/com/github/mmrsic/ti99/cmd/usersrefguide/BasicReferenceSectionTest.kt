package com.github.mmrsic.ti99.cmd.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
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

}