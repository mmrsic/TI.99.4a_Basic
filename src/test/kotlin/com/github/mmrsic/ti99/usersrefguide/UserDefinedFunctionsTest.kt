package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.KeyboardInputProvider
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on pages II-105 through II-107.
 */
class UserDefinedFunctionsTest {

    @Test
    fun testPi() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 DEF PI=4*ATN(1)
            110 PRINT COS(60*PI/180)
            120 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                14 to "  TI BASIC READY",
                16 to " >100 DEF PI=4*ATN(1)",
                17 to " >110 PRINT COS(60*PI/180)",
                18 to " >120 END",
                19 to " >RUN",
                20 to "   .5",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testEvaluateCurrentVariableValue() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 REM EVALUATE Y=X*(X-3)
            110 DEF Y=X*(X-3)
            120 PRINT " X  Y"
            130 FOR X=-2 TO 5
            140 PRINT X;Y
            150 NEXT X
            160 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                2 to "  TI BASIC READY",
                4 to " >100 REM EVALUATE Y=X*(X-3)",
                5 to " >110 DEF Y=X*(X-3)",
                6 to " >120 PRINT \" X  Y\"",
                7 to " >130 FOR X=-2 TO 5",
                8 to " >140 PRINT X;Y",
                9 to " >150 NEXT X",
                10 to " >160 END",
                11 to " >RUN",
                12 to "   X  Y",
                13 to "  -2  10",
                14 to "  -1  4",
                15 to "   0  0",
                16 to "   1 -2",
                17 to "   2 -2",
                18 to "   3  0",
                19 to "   4  4",
                20 to "   5  10",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testPrintNameBackwards() {
        val machine = TiBasicModule().apply {
            setKeyboardInputProvider(object : KeyboardInputProvider {
                override fun provideInput(ctx: KeyboardInputProvider.InputContext): Sequence<Char> {
                    return when (ctx.prompt) {
                        "NAME? " -> "ROBOT\r".asSequence()
                        else -> throw IllegalArgumentException("Unable to provide input for ${ctx.prompt}")
                    }
                }
            })
        }
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 REM TAKE A NAME AND     PRINT IT BACKWARDS
            110 DEF BACK$(X)=SEG$(NAME$,X,1)
            120 INPUT "NAME? ":NAME$
            130 FOR I=LEN(NAME$) TO 1 STEP -1
            140 BNAME$=BNAME$&BACK$(I)
            150 NEXT I
            160 PRINT NAME$:BNAME$
            170 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                4 to "  TI BASIC READY",
                6 to " >100 REM TAKE A NAME AND",
                7 to "  PRINT IT BACKWARDS",
                8 to " >110 DEF BACK$(X)=SEG$(NAME$,",
                9 to "  X,1)",
                10 to " >120 INPUT \"NAME? \":NAME$",
                11 to " >130 FOR I=LEN(NAME$) TO 1 ST",
                12 to "  EP -1",
                13 to " >140 BNAME$=BNAME$&BACK$(I)",
                14 to " >150 NEXT I",
                15 to " >160 PRINT NAME$:BNAME$",
                16 to " >170 END",
                17 to " >RUN",
                18 to "  NAME? ROBOT",
                19 to "  ROBOT",
                20 to "  TOBOR",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testParameterHidesVariable() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 DEF FUNC(A)=A*(A+B-5)
            110 A=6.9
            120 B=13
            130 PRINT "B= ";B:"FUNC(3)= ";FUNC(3):"A= ";A
            140 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                9 to "  TI BASIC READY",
                11 to " >100 DEF FUNC(A)=A*(A+B-5)",
                12 to " >110 A=6.9",
                13 to " >120 B=13",
                14 to " >130 PRINT \"B= \";B:\"FUNC(3)=",
                15 to "  \";FUNC(3):\"A= \";A",
                16 to " >140 END",
                17 to " >RUN",
                18 to "  B=  13",
                19 to "  FUNC(3)=  33",
                20 to "  A=  6.9",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

}