package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on pages II-84 and II-85.
 */
class CallSoundTest {

    @Test
    fun testCommandTenthOfASecond() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpret("CALL SOUND(100,294,2)", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                20 to "  TI BASIC READY",
                22 to " >CALL SOUND(100,294,2)",
                24 to " >"
            ), machine.screen
        )
        TestHelperScreen.assertCursorAt(24, 3, machine.screen)
    }

    @Test
    fun testRepeatedStatementWithNegativeDuration() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 TONE=110
            110 FOR COUNT=1 TO 10
            120 CALL SOUND(-500,TONE,1)
            130 TONE=TONE+110
            140 NEXT COUNT
            150 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                12 to "  TI BASIC READY",
                14 to " >100 TONE=110",
                15 to " >110 FOR COUNT=1 TO 10",
                16 to " >120 CALL SOUND(-500,TONE,1)",
                17 to " >130 TONE=TONE+110",
                18 to " >140 NEXT COUNT",
                19 to " >150 END",
                20 to " >RUN",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testRepeatedStatementWithPositiveDuration() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 TONE=110
            110 FOR COUNT=1 TO 10
            120 CALL SOUND(+500,TONE,1)
            130 TONE=TONE+110
            140 NEXT COUNT
            150 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                12 to "  TI BASIC READY",
                14 to " >100 TONE=110",
                15 to " >110 FOR COUNT=1 TO 10",
                16 to " >120 CALL SOUND(+500,TONE,1)",
                17 to " >130 TONE=TONE+110",
                18 to " >140 NEXT COUNT",
                19 to " >150 END",
                20 to " >RUN",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

}