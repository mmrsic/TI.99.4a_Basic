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

}