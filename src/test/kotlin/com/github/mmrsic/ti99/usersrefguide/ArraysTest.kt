package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found is User's Reference Guide on pages II-108 through II-112.
 */
class ArraysTest {

    @Test
    fun testPairTwoLists() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 REM THIS PROGRAM PAIRS TWO LISTS
            110 REM LINES 120 TO 150    ASSIGN VALUES TO LIST X
            120 FOR T=1 TO 4
            130 READ X(T)
            140 NEXT T
            150 DATA 1,3,5,7
            160 REM LINES 170 TO 200    ASSIGN VALUES TO LIST Y
            170 FOR S=1 TO 4
            180 READ Y(S)
            190 NEXT S
            200 DATA 2,4,6,8
            210 REM LINES 220 TO 270    PAIR THE LINES AND PRINT    THE COMBINATIONS
            220 FOR T=1 TO 4
            230 FOR S=1 TO 4
            240 PRINT X(T);Y(S);" ";
            250 NEXT S
            260 PRINT
            270 NEXT T
            280 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                1 to "  ASSIGN VALUES TO LIST Y",
                2 to " >170 FOR S=1 TO 4",
                3 to " >180 READ Y(S)",
                4 to " >190 NEXT S",
                5 to " >200 DATA 2,4,6,8",
                6 to " >210 REM LINES 220 TO 270",
                7 to "  PAIR THE LINES AND PRINT",
                8 to "  THE COMBINATIONS",
                9 to " >220 FOR T=1 TO 4",
                10 to " >230 FOR S=1 TO 4",
                11 to " >240 PRINT X(T);Y(S);\" \";",
                12 to " >250 NEXT S",
                13 to " >260 PRINT",
                14 to " >270 NEXT T",
                15 to " >280 END",
                16 to " >RUN",
                17 to "   1  2   1  4   1  6   1  8",
                18 to "   3  2   3  4   3  6   3  8",
                19 to "   5  2   5  4   5  6   5  8",
                20 to "   7  2   7  4   7  6   7  8",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testMultiplicationTable() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 REM MULTIPLICATION TABLE
            110 CALL CLEAR
            120 CALL CHAR(96,"FF")
            130 CALL CHAR(97,"8080808080808080")
            140 CALL CHAR(98,"FF80808080808080")
            150 FOR A=1 TO 5
            160 FOR B=1 TO 5
            170 M(A,B)=A*B
            180 NEXT B
            190 NEXT A
            200 FOR A=1 TO 5
            210 FOR B=1 TO 5
            220 PRINT M(A,B);
            230 IF B<>1 THEN 250 
            240 PRINT CHR$(97);" ";
            250 NEXT B
            260 PRINT 
            270 REM THE FOLLOWING       STATEMENTS PRINT THE LINES  DEFINING THE TABLE
            280 IF A<>1 THEN 330
            290 PRINT
            300 CALL HCHAR(23,3,96,3)
            310 CALL HCHAR(23,6,98)
            320 CALL HCHAR(23,7,96,16)
            330 NEXT A
            340 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                15 to "   1 a  2  3  4  5",
                16 to "  ```b````````````````",
                17 to "   2 a  4  6  8  10",
                18 to "   3 a  6  9  12  15",
                19 to "   4 a  8  12  16  20",
                20 to "   5 a  10  15  20  25",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }

    @Test
    fun testDimension() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 DIM X(15)
            110 FOR I=1 TO 15
            120 READ X(I)
            130 NEXT I
            140 REM PRINT LOOP
            150 FOR I=15 TO 1 STEP -1
            160 PRINT X(I);
            170 NEXT I
            180 DATA 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15
            190 END
            RUN
            """.trimIndent(), machine
        )

        TestHelperScreen.assertPrintContents(
            mapOf(
                6 to "  TI BASIC READY",
                8 to " >100 DIM X(15)",
                9 to " >110 FOR I=1 TO 15",
                10 to " >120 READ X(I)",
                11 to " >130 NEXT I",
                12 to " >140 REM PRINT LOOP",
                13 to " >150 FOR I=15 TO 1 STEP -1",
                14 to " >160 PRINT X(I);",
                15 to " >170 NEXT I",
                16 to " >180 DATA 1,2,3,4,5,6,7,8,9,1",
                17 to "  0,11,12,13,14,15",
                18 to " >190 END",
                19 to " >RUN",
                20 to "   15  14  13  12  11  10  9",
                21 to "   8  7  6  5  4  3  2  1",
                22 to "  ** DONE **",
                24 to " >"
            ), machine.screen
        )
    }
}