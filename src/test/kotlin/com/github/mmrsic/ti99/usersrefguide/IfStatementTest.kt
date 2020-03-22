package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.KeyboardInputProvider
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on pages II-51 and II-52.
 */
class IfStatementTest {

    @Test
    fun testFindTheLargestInputValue() {
        val machine = TiBasicModule().apply {
            setKeyboardInputProvider(object : KeyboardInputProvider {
                override fun provideInput(ctx: KeyboardInputProvider.InputContext): Sequence<Char> {
                    println("Providing keyboard input for line=${ctx.programLine}, call=${ctx.programLineCalls}")
                    return when (ctx.programLine) {
                        110 -> "3\r"
                        120 -> "456\r"
                        160 -> when (ctx.programLineCalls) {
                            1 -> "321\r"
                            2 -> "292\r"
                            else -> throw IllegalArgumentException("Unexpected program line call #${ctx.programLineCalls}")
                        }
                        else -> throw IllegalArgumentException("Unexpected program line: ${ctx.programLine}")
                    }.asSequence()
                }
            })
        }
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 REM FIND THE LARGEST OF A SET OF NUMBERS
            110 INPUT "HOW MANY VALUES?":N
            120 INPUT "VALUE?":A
            130 L=A
            140 N=N-1
            150 IF N<=0 THEN 180
            160 INPUT "VALUE?":A
            170 IF L>A THEN 140 ELSE 130
            180 PRINT L;"IS THE LARGEST"
            190 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                1 to " >100 REM FIND THE LARGEST OF",
                2 to "  A SET OF NUMBERS",
                3 to " >110 INPUT \"HOW MANY VALUES?\"",
                4 to "  :N",
                5 to " >120 INPUT \"VALUE?\":A",
                6 to " >130 L=A",
                7 to " >140 N=N-1",
                8 to " >150 IF N<=0 THEN 180",
                9 to " >160 INPUT \"VALUE?\":A",
                10 to " >170 IF L>A THEN 140 ELSE 130",
                12 to " >180 PRINT L;\"IS THE LARGEST\"",
                14 to " >190 END",
                15 to " >RUN",
                16 to "  HOW MANY VALUES?3",
                17 to "  VALUE?456",
                18 to "  VALUE?321",
                19 to "  VALUE?292",
                20 to "   456 IS THE LARGEST",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }


    @Test
    fun testStringComparison() {
        val machine = TiBasicModule().apply {
            setKeyboardInputProvider(object : KeyboardInputProvider {
                override fun provideInput(ctx: KeyboardInputProvider.InputContext): Sequence<Char> {
                    println("Providing input for program line #${ctx.programLine}")
                    return when (ctx.programLine) {
                        100 -> "TEXAS\r".asSequence()
                        110 -> "TEX\r".asSequence()
                        else -> super.provideInput(ctx)
                    }
                }
            })
        }
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 INPUT "A$ IS ":A$
            110 INPUT "B$ IS ":B$
            120 IF A$=B$ THEN 160
            130 IF A$<B$ THEN 180
            140 PRINT "B$ IS LESS"
            150 GOTO 190
            160 PRINT "A$=B$"
            170 GOTO 190
            180 PRINT "B$ IS GREATER"
            190 END
            RUN
            """.trimIndent(), machine
        )

        machine.setKeyboardInputProvider(object : KeyboardInputProvider {
            override fun provideInput(ctx: KeyboardInputProvider.InputContext): Sequence<Char> {
                println("Providing input for program line #${ctx.programLine}")
                return when (ctx.programLine) {
                    100 -> "TAXES\r".asSequence()
                    110 -> "TEX\r".asSequence()
                    else -> super.provideInput(ctx)
                }
            }
        })
        interpreter.interpret("RUN", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                1 to " >110 INPUT \"B\$ IS \":B\$",
                2 to " >120 IF A\$=B\$ THEN 160",
                3 to " >130 IF A\$<B\$ THEN 180",
                4 to " >140 PRINT \"B\$ IS LESS\"",
                5 to " >150 GOTO 190",
                6 to " >160 PRINT \"A\$=B\$\"",
                7 to " >170 GOTO 190",
                8 to " >180 PRINT \"B$ IS GREATER\"",
                9 to " >190 END",
                10 to " >RUN",
                11 to "  A$ IS TEXAS",
                12 to "  B$ IS TEX",
                13 to "  B$ IS LESS",
                15 to "  ** DONE **",
                17 to " >RUN",
                18 to "  A$ IS TAXES",
                19 to "  B$ IS TEX",
                20 to "  B$ IS GREATER",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testAlternativeFormat() {
        val machine = TiBasicModule().apply {
            setKeyboardInputProvider(object : KeyboardInputProvider {
                override fun provideInput(ctx: KeyboardInputProvider.InputContext): Sequence<Char> {
                    return when (ctx.programLine) {
                        100 -> when (ctx.programLineCalls) {
                            1, 2 -> "2\r"
                            else -> String(charArrayOf(2.toChar(), '\r'))
                        }
                        else -> when (ctx.programLineCalls) {
                            1 -> "3\r"
                            else -> "-2\r"
                        }
                    }.asSequence()
                }
            })
        }
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 INPUT "A IS ":A
            110 INPUT "B IS ":B
            120 IF A+B THEN 150
            130 PRINT "RESULT IS ZERO,EXPRESSION FALSE"
            140 GOTO 100
            150 PRINT "RESULT IS NON-ZERO,EXPRESSION TRUE"
            160 GO TO 100
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                2 to "  TI BASIC READY",
                4 to " >100 INPUT \"A IS \":A",
                5 to " >110 INPUT \"B IS \":B",
                6 to " >120 IF A+B THEN 150",
                7 to " >130 PRINT \"RESULT IS ZERO,EX",
                8 to "  PRESSION FALSE\"",
                9 to " >140 GOTO 100",
                10 to " >150 PRINT \"RESULT IS NON-ZER",
                11 to "  O,EXPRESSION TRUE\"",
                12 to " >160 GO TO 100",
                13 to " >RUN",
                14 to "  A IS 2",
                15 to "  B IS 3",
                16 to "  RESULT IS NON-ZERO,EXPRESSIO",
                17 to "  N TRUE",
                18 to "  A IS 2",
                19 to "  B IS -2",
                20 to "  RESULT IS ZERO,EXPRESSION FA",
                21 to "  LSE",
                22 to "  A IS",
                23 to "  * BREAKPOINT AT 100",
                24 to " >"
            ), machine.screen
        )
    }

}