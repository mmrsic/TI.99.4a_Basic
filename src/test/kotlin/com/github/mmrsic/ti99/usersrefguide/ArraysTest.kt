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

}