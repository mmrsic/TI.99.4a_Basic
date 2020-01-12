package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases found in User Reference Guide in section Line Numbers on page II-8.
 */
class LineNumbersTest {

    @Test
    fun testSequentialLineNumbers() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpret("100 A=27.9", machine)
        interpreter.interpret("110 B=31.8", machine)
        interpreter.interpret("120 PRINT A;B", machine)
        interpreter.interpret("130 END", machine)
        interpreter.interpret("RUN", machine)

        TestHelperScreen.assertPrintContents(
            mapOf(
                13 to "  TI BASIC READY",
                15 to " >100 A=27.9",
                16 to " >110 B=31.8",
                17 to " >120 PRINT A;B",
                18 to " >130 END",
                19 to " >RUN",
                20 to "   27.9  31.8",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testLineNumberToLow() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpret("0 A=2", machine)
        TestHelperScreen.assertPrintContents(
            mapOf(
                18 to "  TI BASIC READY",
                20 to " >0 A=2",
                22 to "  * BAD LINE NUMBER",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testLineNumberTooHigh() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpret("33000 C=4", machine)
        TestHelperScreen.assertPrintContents(
            mapOf(
                18 to "  TI BASIC READY",
                20 to " >33000 C=4",
                22 to "  * BAD LINE NUMBER",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testLineNumberWithLeadingZero() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpret("01 PRINT", machine)
        interpreter.interpret("002 PRINT", machine)
        interpreter.interpret("0003 PRINT", machine)
        interpreter.interpret("LIST", machine)
        TestHelperScreen.assertPrintContents(
            mapOf(
                15 to "  TI BASIC READY",
                17 to " >01 PRINT",
                18 to " >002 PRINT",
                19 to " >0003 PRINT",
                20 to " >LIST",
                21 to "  1 PRINT",
                22 to "  2 PRINT",
                23 to "  3 PRINT",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testLineNumbersUnsorted() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpret("50 PRINT 2", machine)
        interpreter.interpret("25 PRINT 1", machine)
        interpreter.interpret("60 PRINT 3", machine)
        interpreter.interpret("RUN", machine)
        TestHelperScreen.assertPrintContents(
            mapOf(
                12 to "  TI BASIC READY",
                14 to " >50 PRINT 2",
                15 to " >25 PRINT 1",
                16 to " >60 PRINT 3",
                17 to " >RUN",
                18 to "   1",
                19 to "   2",
                20 to "   3",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

}