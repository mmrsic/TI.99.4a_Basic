package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.CodeSequenceProvider
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on pages from II-58 to II-60.
 */
class InputStatementTest {

    @Test
    fun testSimpleNumberWithoutPrompt() {
        val machine = TiBasicModule().apply {
            setKeyboardInputProvider(object : CodeSequenceProvider {
                override fun provideInput(ctx: CodeSequenceProvider.Context) = "25\r".asSequence()
            })
        }
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 INPUT B
            110 PRINT B
            120 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                13 to "  TI BASIC READY",
                15 to " >100 INPUT B",
                16 to " >110 PRINT B",
                17 to " >120 END",
                18 to " >RUN",
                19 to "  ? 25",
                20 to "   25",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testVariousPrompts() {
        val machine = TiBasicModule().apply {
            setKeyboardInputProvider(object : CodeSequenceProvider {
                override fun provideInput(ctx: CodeSequenceProvider.Context): Sequence<Char> {
                    return when (ctx.prompt) {
                        "COST OF CAR?" -> "5500\r"
                        "TAX?" -> "500\r"
                        "SALES TAX?" -> "500\r"
                        else -> throw IllegalArgumentException("Unexpected prompt ${ctx.prompt}")
                    }.asSequence()
                }
            })
        }
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 INPUT "COST OF CAR?":B
            110 A$="TAX?"
            120 INPUT A$:C
            130 INPUT "SALES "&A$:X
            140 PRINT B;C;X
            150 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                8 to "  TI BASIC READY",
                10 to " >100 INPUT \"COST OF CAR?\":B",
                11 to " >110 A$=\"TAX?\"",
                12 to " >120 INPUT A$:C",
                13 to " >130 INPUT \"SALES \"&A$:X",
                14 to " >140 PRINT B;C;X",
                15 to " >150 END",
                16 to " >RUN",
                17 to "  COST OF CAR?5500",
                18 to "  TAX?500",
                19 to "  SALES TAX?500",
                20 to "   5500  500  500",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testVariableList() {
        val machine = TiBasicModule().apply {
            setKeyboardInputProvider(object : CodeSequenceProvider {
                override fun provideInput(ctx: CodeSequenceProvider.Context) = "10,HELLO,25,3.2\r".asSequence()
            })
        }
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
                100 INPUT A,B$,C,D
                110 PRINT A:B$:C:D
                120 END
                RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                10 to "  TI BASIC READY",
                12 to " >100 INPUT A,B$,C,D",
                13 to " >110 PRINT A:B$:C:D",
                14 to " >120 END",
                15 to " >RUN",
                16 to "  ? 10,HELLO,25,3.2",
                17 to "   10",
                18 to "  HELLO",
                19 to "   25",
                20 to "   3.2",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testQuotedStringInputOptions() {
        val machine = TiBasicModule()
        machine.setKeyboardInputProvider(object : CodeSequenceProvider {
            override fun provideInput(ctx: CodeSequenceProvider.Context): Sequence<Char> {
                return (when (ctx.programLine) {
                    100 -> "\"JONES, MARY\""
                    120 -> "\"\"\"HELLO THERE\"\"\""
                    140 -> "\"JAMES B. SMITH, JR.\""
                    160 -> "\"SELLING PRICE IS \""
                    190 -> "TEXAS"
                    else -> throw IllegalArgumentException("Unexpected program line: ${ctx.programLine}")
                } + "\r").asSequence()
            }
        })
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
                100 INPUT A$
                110 PRINT A$::
                120 INPUT B$
                130 PRINT B$::
                140 INPUT C$
                150 PRINT C$::
                160 INPUT D$
                170 X=500
                180 PRINT D$;X::
                190 INPUT E$
                200 PRINT E$
                210 END
                RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                1 to " >170 X=500",
                2 to " >180 PRINT D$;X::",
                3 to " >190 INPUT E$",
                4 to " >200 PRINT E$",
                5 to " >210 END",
                6 to " >RUN",
                7 to "  ? \"JONES, MARY\"",
                8 to "  JONES, MARY",
                10 to "  ? \"\"\"HELLO THERE\"\"\"",
                11 to "  \"HELLO THERE\"",
                13 to "  ? \"JAMES B. SMITH, JR.\"",
                14 to "  JAMES B. SMITH, JR.",
                16 to "  ? \"SELLING PRICE IS \"",
                17 to "  SELLING PRICE IS  500",
                19 to "  ? TEXAS",
                20 to "  TEXAS",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

}