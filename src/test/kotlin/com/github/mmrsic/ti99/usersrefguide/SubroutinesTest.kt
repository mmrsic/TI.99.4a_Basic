package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on pages II-114 through II-117.
 */
class SubroutinesTest {

    @Test
    fun testSubroutineToPrintArray() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 REM BUILD AN ARRAY,     MULTIPLY EACH ELEMENT BY 3, PRINT BOTH ARRAYS
            110 FOR X=1 TO 4
            120 FOR Y=1 TO 7
            130 I(X,Y)=INT(30*RND)+1
            140 NEXT Y
            150 NEXT X
            160 PRINT "FIRST ARRAY":
            170 GOSUB 260
            180 FOR X=1 TO 4
            190 FOR Y=1 TO 7
            200 I(X,Y)=3*I(X,Y)
            210 NEXT Y
            220 NEXT X
            230 PRINT "3 TIMES VALUES IN FIRST ARRAY"::
            240 GOSUB 260
            250 STOP
            260 REM SUBROUTINE TO PRINT ARRAY
            270 FOR X=1 TO 4
            280 FOR Y=1 TO 7
            290 PRINT I(X,Y);
            300 NEXT Y
            310 PRINT
            320 NEXT X
            330 PRINT
            340 RETURN
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                1 to " >300 NEXT Y",
                2 to " >310 PRINT",
                3 to " >320 NEXT X",
                4 to " >330 PRINT",
                5 to " >340 RETURN",
                6 to " >RUN",
                7 to "  FIRST ARRAY",
                8 to "   16  12  17  12  8  17  8",
                9 to "   18  22  1  29  16  14  11",
                10 to "   5  25  22  4  24  11  24",
                11 to "   26  21  18  2  12  20  15",
                13 to "  3 TIMES VALUES IN FIRST ARRA",
                14 to "  Y",
                16 to "   48  36  51  36  24  51  24",
                17 to "   54  66  3  87  48  42  33",
                18 to "   15  75  66  12  72  33  72",
                19 to "   78  63  54  6  36  60  45",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testGosubOwnLineNumberCausesMemoryFull() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 X=12
            110 Y=23
            120 GOSUB 120
            130 PRINT Z
            140 STOP
            150 REM SUBROUTINE
            160 Z=X+Y*120/5
            170 RETURN
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                10 to "  TI BASIC READY",
                12 to " >100 X=12",
                13 to " >110 Y=23",
                14 to " >120 GOSUB 120",
                15 to " >130 PRINT Z",
                16 to " >140 STOP",
                17 to " >150 REM SUBROUTINE",
                18 to " >160 Z=X+Y*120/5",
                19 to " >170 RETURN",
                20 to " >RUN",
                22 to "  * MEMORY FULL IN 120",
                24 to " >"
            ), machine.screen
        )

        interpreter.interpretAll(listOf("120 GOSUB 150", "CALL CLEAR", "RUN"), machine)
        TestHelperScreen.assertPrintContents(
            mapOf(
                19 to " >RUN",
                20 to "   564",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testReturn() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 FOR I=1 TO 3
            110 GOSUB 150
            120 PRINT "I=";I
            130 NEXT I
            140 STOP
            150 REM SUBROUTINE
            160 FOR X=1 TO 2
            170 PRINT "X=";X
            180 NEXT X
            190 RETURN
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                1 to " >100 FOR I=1 TO 3",
                2 to " >110 GOSUB 150",
                3 to " >120 PRINT \"I=\";I",
                4 to " >130 NEXT I",
                5 to " >140 STOP",
                6 to " >150 REM SUBROUTINE",
                7 to " >160 FOR X=1 TO 2",
                8 to " >170 PRINT \"X=\";X",
                9 to " >180 NEXT X",
                10 to " >190 RETURN",
                11 to " >RUN",
                12 to "  X= 1",
                13 to "  X= 2",
                14 to "  I= 1",
                15 to "  X= 1",
                16 to "  X= 2",
                17 to "  I= 2",
                18 to "  X= 1",
                19 to "  X= 2",
                20 to "  I= 3",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }
}