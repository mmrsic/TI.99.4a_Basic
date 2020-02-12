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

    @Test
    fun testExampleOfFractionalIncrement() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 REM EXAMPLE OF          FRACTIONAL INCREMENT
            110 FOR X=.1 TO 1 STEP .2
            120 PRINT X;
            130 NEXT X
            140 PRINT :X
            150 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                9 to "  TI BASIC READY",
                11 to " >100 REM EXAMPLE OF",
                12 to "  FRACTIONAL INCREMENT",
                13 to " >110 FOR X=.1 TO 1 STEP .2",
                14 to " >120 PRINT X;",
                15 to " >130 NEXT X",
                16 to " >140 PRINT :X",
                17 to " >150 END",
                18 to " >RUN",
                19 to "   .1  .3  .5  .7  .9",
                20 to "   1.1",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testChangeLimitWhileLooping() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 L=5
            110 FOR I=1 TO L
            120 L=20
            130 PRINT L;I
            140 NEXT I
            150 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                7 to "  TI BASIC READY",
                9 to " >100 L=5",
                10 to " >110 FOR I=1 TO L",
                11 to " >120 L=20",
                12 to " >130 PRINT L;I",
                13 to " >140 NEXT I",
                14 to " >150 END",
                15 to " >RUN",
                16 to "   20  1",
                17 to "   20  2",
                18 to "   20  3",
                19 to "   20  4",
                20 to "   20  5",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testChangeControlVariableWhileLoopIsPerformed() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 FOR I=1 TO 10
            110 I=I+1
            120 PRINT I
            130 NEXT I
            140 PRINT I
            150 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                6 to "  TI BASIC READY",
                8 to " >100 FOR I=1 TO 10",
                9 to " >110 I=I+1",
                10 to " >120 PRINT I",
                11 to " >130 NEXT I",
                12 to " >140 PRINT I",
                13 to " >150 END",
                14 to " >RUN",
                15 to "   2",
                16 to "   4",
                17 to "   6",
                18 to "   8",
                19 to "   10",
                20 to "   11",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

}