package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases found in User Reference Guide in section String Expressions on page II-15.
 */
class StringExpressionsTest {

    @Test
    fun testConcatenationUsingSegFunction() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            NEW
            100 A$="HI"
            110 B$="HELLO THERE!"
            120 C$="HOW ARE YOU?"
            130 MSG$=A$&SEG$(B$,6,7)
            140 PRINT MSG$&" "&C$
            150 END
            RUN
            """.trimIndent()
            , machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                11 to "  TI BASIC READY",
                13 to " >100 A$=\"HI\"",
                14 to " >110 B$=\"HELLO THERE!\"",
                15 to " >120 C$=\"HOW ARE YOU?\"",
                16 to " >130 MSG$=A$&SEG$(B$,6,7)",
                17 to " >140 PRINT MSG$&\" \"&C$",
                18 to " >150 END",
                19 to " >RUN",
                20 to "  HI THERE! HOW ARE YOU?",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )

    }

}