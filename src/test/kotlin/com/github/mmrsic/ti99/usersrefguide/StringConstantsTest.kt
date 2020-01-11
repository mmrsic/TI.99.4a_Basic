package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases found in User Reference Guide in section String Constants on page II-10.
 */
class StringConstantsTest {

    @Test
    fun testExampleStringConstants() {
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
    fun testStringConstantsWithQuotes() {
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