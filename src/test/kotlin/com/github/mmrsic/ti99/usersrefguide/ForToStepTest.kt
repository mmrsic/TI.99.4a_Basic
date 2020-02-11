package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.CodeSequenceProvider
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on pages ranging from II-53 to II-55.
 */
class ForToStepTest {

    @Test
    fun testComputingSimpleInterestForTenYears() {
        val machine = TiBasicModule().apply {
            setKeyboardInputProvider(object : CodeSequenceProvider {
                override fun provideInput(ctx: CodeSequenceProvider.Context): Sequence<Char> {
                    return when (ctx.programLine) {
                        110 -> "100\r"
                        120 -> ".0775\r"
                        else -> throw IllegalArgumentException("Cannot provide input for program line #${ctx.programLine}")
                    }.asSequence()
                }
            })
        }
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 REM COMPUTING SIMPLE INTEREST FOR 10 YEARS
            110 INPUT "PRINCIPLE? ":P
            120 INPUT "RATE? ":R
            130 FOR YEARS=1 TO 10
            140 P=P+(P*R)
            150 NEXT YEARS
            160 P=INT(P*100+.5)/100
            170 PRINT P
            180 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                5 to "  TI BASIC READY",
                7 to " >100 REM COMPUTING SIMPLE INT",
                8 to "  EREST FOR 10 YEARS",
                9 to " >110 INPUT \"PRINCIPLE? \":P",
                10 to " >120 INPUT \"RATE? \":R",
                11 to " >130 FOR YEARS=1 TO 10",
                12 to " >140 P=P+(P*R)",
                13 to " >150 NEXT YEARS",
                14 to " >160 P=INT(P*100+.5)/100",
                15 to " >170 PRINT P",
                16 to " >180 END",
                17 to " >RUN",
                18 to "  PRINCIPLE? 100",
                19 to "  RATE? .0775",
                20 to "   210.95",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

}