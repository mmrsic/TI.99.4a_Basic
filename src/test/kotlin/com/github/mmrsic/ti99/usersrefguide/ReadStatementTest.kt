package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on pages II-61 and II-62.
 */
class ReadStatementTest {

   @Test
   fun testTwoNumericVariables() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
                100 FOR I=1 TO 3
                110 READ X,Y
                120 PRINT X;Y
                130 NEXT I
                140 DATA 22,15,36,52,48,96.5
                150 END
                RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            8 to "  TI BASIC READY",
            10 to " >100 FOR I=1 TO 3",
            11 to " >110 READ X,Y",
            12 to " >120 PRINT X;Y",
            13 to " >130 NEXT I",
            14 to " >140 DATA 22,15,36,52,48,96.5",
            16 to " >150 END",
            17 to " >RUN",
            18 to "   22  15",
            19 to "   36  52",
            20 to "   48  96.5",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testSubscriptExpressionsAreEvaluatedAfterVariablesToTheLeftHaveBeenAssigned() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
                100 READ I,A(I)
                110 DATA 2,35
                120 PRINT A(2)
                130 END
                RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            13 to "  TI BASIC READY",
            15 to " >100 READ I,A(I)",
            16 to " >110 DATA 2,35",
            17 to " >120 PRINT A(2)",
            18 to " >130 END",
            19 to " >RUN",
            20 to "   35",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testRestoreWithLineNumber() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 FOR I=1 TO 2
            110 FOR J=1 TO 4
            120 READ A,B
            130 PRINT A;B;
            140 NEXT J
            150 PRINT
            160 RESTORE 190
            170 NEXT I
            180 DATA 2,4,6,8,10
            190 DATA 12,14,16,18
            200 DATA 20,22,24,26
            210 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            3 to "  TI BASIC READY",
            5 to " >100 FOR I=1 TO 2",
            6 to " >110 FOR J=1 TO 4",
            7 to " >120 READ A,B",
            8 to " >130 PRINT A;B;",
            9 to " >140 NEXT J",
            10 to " >150 PRINT",
            11 to " >160 RESTORE 190",
            12 to " >170 NEXT I",
            13 to " >180 DATA 2,4,6,8,10",
            14 to " >190 DATA 12,14,16,18",
            15 to " >200 DATA 20,22,24,26",
            16 to " >210 END",
            17 to " >RUN",
            18 to "   2  4  6  8  10  12  14  16",
            19 to "   12  14  16  18  20  22  24",
            20 to "   26",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testDataError() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 READ A,B
            110 DATA 12,HELLO
            120 PRINT A;B
            130 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            14 to "  TI BASIC READY",
            16 to " >100 READ A,B",
            17 to " >110 DATA 12,HELLO",
            18 to " >120 PRINT A;B",
            19 to " >130 END",
            20 to " >RUN",
            22 to "  * DATA ERROR IN 100",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testUnderflowAndOverflowAndNoMoreRemaining() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 READ A,B
            110 DATA 12E-135
            120 DATA 36E142
            130 PRINT :A:B
            140 READ C
            150 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            6 to "  TI BASIC READY",
            8 to " >100 READ A,B",
            9 to " >110 DATA 12E-135",
            10 to " >120 DATA 36E142",
            11 to " >130 PRINT :A:B",
            12 to " >140 READ C",
            13 to " >150 END",
            14 to " >RUN",
            16 to "  * WARNING:",
            17 to "    NUMBER TOO BIG IN 100",
            19 to "   0",
            20 to "   9.99999E+**",
            22 to "  * DATA ERROR IN 140",
            24 to " >"
         ), machine.screen
      )
   }

}