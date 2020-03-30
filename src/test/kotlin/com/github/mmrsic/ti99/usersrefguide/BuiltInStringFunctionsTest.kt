package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on pages II-100 through II-103.
 */
class BuiltInStringFunctionsTest {

    @Test
    fun testAsciiValue() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 A$="HELLO"
            110 C$="JACK SPRAT"
            120 C=ASC(C$)
            130 B$="THE ASCII VALUE OF "
            140 PRINT B$;"H IS";ASC(A$)
            150 PRINT B$;"J IS";C
            160 PRINT B$;"N IS";ASC("NAME")
            170 PRINT B$;"1 IS";ASC("1")
            180 PRINT CHR$(ASC(A$))
            190 END
            RUN
            """.trimIndent(), machine
        )

        // Note that the example in the User's Reference Guide is wrong:
        // On a real TI 99/4a, The printed texts are aligned at column three, not at column four
        TestHelperScreen.assertPrintContents(
            mapOf(
                2 to " >100 A$=\"HELLO\"",
                3 to " >110 C$=\"JACK SPRAT\"",
                4 to " >120 C=ASC(C$)",
                5 to " >130 B$=\"THE ASCII VALUE OF \"",
                7 to " >140 PRINT B$;\"H IS\";ASC(A$)",
                8 to " >150 PRINT B$;\"J IS\";C",
                9 to " >160 PRINT B$;\"N IS\";ASC(\"NAM",
                10 to "  E\")",
                11 to " >170 PRINT B$;\"1 IS\";ASC(\"1\")",
                13 to " >180 PRINT CHR$(ASC(A$))",
                14 to " >190 END",
                15 to " >RUN",
                16 to "  THE ASCII VALUE OF H IS 72",
                17 to "  THE ASCII VALUE OF J IS 74",
                18 to "  THE ASCII VALUE OF N IS 78",
                19 to "  THE ASCII VALUE OF 1 IS 49",
                20 to "  H",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testCharacter() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 A$=CHR$(72)&CHR$(73)&CHR$(33)
            110 PRINT A$
            120 CALL CHAR(97,"0103070F1F3F7FFF")
            130 PRINT CHR$(32);CHR$(97)
            140 PRINT CHR$(3*14)
            150 PRINT CHR$(ASC("+"))
            160 END
            RUN
            PRINT CHR$(33010)
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                1 to "  TI BASIC READY",
                3 to " >100 A$=CHR$(72)&CHR$(73)&CHR",
                4 to "  $(33)",
                5 to " >110 PRINT A$",
                6 to " >120 CALL CHAR(97,\"0103070F1F",
                7 to "  3F7FFF\")",
                8 to " >130 PRINT CHR$(32);CHR$(97)",
                9 to " >140 PRINT CHR$(3*14)",
                10 to " >150 PRINT CHR$(ASC(\"+\"))",
                11 to " >160 END",
                12 to " >RUN",
                13 to "  HI!",
                14 to "   a",
                15 to "  *",
                16 to "  +",
                18 to "  ** DONE **",
                20 to " >PRINT CHR$(33010)",
                22 to "  * BAD VALUE",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testLength() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 NAME$="CATHY"
            110 CITY$="NEW YORK"
            120 MSG$="HELLO "&"THERE!"
            130 PRINT NAME$;LEN(NAME$)
            140 PRINT CITY$;LEN(CITY$)
            150 PRINT MSG$;LEN(MSG$)
            160 PRINT LEN(NAME$&CITY$)
            170 PRINT LEN("HI!")
            180 STOP
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                4 to "  TI BASIC READY",
                6 to " >100 NAME$=\"CATHY\"",
                7 to " >110 CITY$=\"NEW YORK\"",
                8 to " >120 MSG$=\"HELLO \"&\"THERE!\"",
                9 to " >130 PRINT NAME$;LEN(NAME$)",
                10 to " >140 PRINT CITY$;LEN(CITY$)",
                11 to " >150 PRINT MSG$;LEN(MSG$)",
                12 to " >160 PRINT LEN(NAME$&CITY$)",
                13 to " >170 PRINT LEN(\"HI!\")",
                14 to " >180 STOP",
                15 to " >RUN",
                16 to "  CATHY 5",
                17 to "  NEW YORK 8",
                18 to "  HELLO THERE! 12",
                19 to "   13",
                20 to "   3",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testPosition() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 MSG$="HELLO THERE! HOW ARE YOU?"
            110 PRINT "H";POS(MSG$,"H",1)
            120 C$="RE"
            130 PRINT C$;POS(MSG$,C$,1);POS(MSG$,C$,12)
            140 PRINT "HI";POS(MSG$,"HI",1)
            150 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                5 to "  TI BASIC READY",
                7 to " >100 MSG$=\"HELLO THERE! HOW A",
                8 to "  RE YOU?\"",
                9 to " >110 PRINT \"H\";POS(MSG$,\"H\",1",
                10 to "  )",
                11 to " >120 C$=\"RE\"",
                12 to " >130 PRINT C$;POS(MSG$,C$,1);",
                13 to "  POS(MSG$,C$,12)",
                14 to " >140 PRINT \"HI\";POS(MSG$,\"HI\"",
                15 to "  ,1)",
                16 to " >150 END",
                17 to " >RUN",
                18 to "  H 1",
                19 to "  RE 10  19",
                20 to "  HI 0",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testStringSegment1() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 MSG$="HELLO THERE! HOW ARE YOU?"
            110 REM SUBSTRING BEGINS IN POSITION 14 AND HAS A LENGTH OF 12.
            120 PRINT SEG$(MSG$,14,12)
            130 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                10 to "  TI BASIC READY",
                12 to " >100 MSG$=\"HELLO THERE! HOW A",
                13 to "  RE YOU?\"",
                14 to " >110 REM SUBSTRING BEGINS IN",
                15 to "  POSITION 14 AND HAS A LENGTH",
                16 to "   OF 12.",
                17 to " >120 PRINT SEG$(MSG$,14,12)",
                18 to " >130 END",
                19 to " >RUN",
                20 to "  HOW ARE YOU?",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testStringSegment2() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 MSG$="I AM A COMPUTER."
            110 PRINT SEG$(MSG$,20,1)
            120 PRINT SEG$(MSG$,10,0)
            130 PRINT SEG$(MSG$,8,20)
            140 END
            RUN
            PRINT SEG$(MSG$,-1,10)
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                6 to "  TI BASIC READY",
                8 to " >100 MSG$=\"I AM A COMPUTER.\"",
                9 to " >110 PRINT SEG$(MSG$,20,1)",
                10 to " >120 PRINT SEG$(MSG$,10,0)",
                11 to " >130 PRINT SEG$(MSG$,8,20)",
                12 to " >140 END",
                13 to " >RUN",
                16 to "  COMPUTER.",
                18 to "  ** DONE **",
                20 to " >PRINT SEG$(MSG$,-1,10)",
                22 to "  * BAD VALUE",
                24 to " >"
            ), machine.screen
        )
    }

}