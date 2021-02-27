package com.github.mmrsic.ti99.programmingbasic

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

class Programs {

   @Test
   fun testExhaustedReadExample() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 READ A,B
            110 LET C=A/B
            120 PRINT C
            130 GOTO 100
            140 DATA 2,1,6,2,90,9,35,7
            150 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            8 to "  TI BASIC READY",
            10 to " >100 READ A,B",
            11 to " >110 LET C=A/B",
            12 to " >120 PRINT C",
            13 to " >130 GOTO 100",
            14 to " >140 DATA 2,1,6,2,90,9,35,7",
            15 to " >150 END",
            16 to " >RUN",
            17 to "   2",
            18 to "   3",
            19 to "   10",
            20 to "   5",
            22 to "  * DATA ERROR IN 100",
            24 to " >"
         ),
         machine.screen
      )
   }

   @Test
   fun testReadAndPrintWithTab() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 LET A=10
         110 READ B
         120 PRINT TAB(A);B;
         130 LET A=A+10
         140 GOTO 110
         150 DATA 1,2,3
         160 END
         RUN
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         """
         |
         |  TI BASIC READY
         | 
         | >100 LET A=10
         | >110 READ B
         | >120 PRINT TAB(A);B;
         | >130 LET A=A+10
         | >140 GOTO 110
         | >150 DATA 1,2,3
         | >160 END
         | >RUN
         |            1         2
         |    3
         |  * DATA ERROR IN 110
         |   
         | >
         """.trimMargin(), machine.screen
      )
   }

   @Test
   fun testStringArrayWithoutDimStatement() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 LET A$(1)="HOUSE"
         110 LET A$(2)="BARN"
         120 LET A$(3)="SHED"
         130 LET A$(4)="STORE"
         140 LET A$(5)="CABIN"
         150 PRINT A$(4)
         160 END
         RUN
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            10 to "  TI BASIC READY",
            12 to " >100 LET A$(1)=\"HOUSE\"",
            13 to " >110 LET A$(2)=\"BARN\"",
            14 to " >120 LET A$(3)=\"SHED\"",
            15 to " >130 LET A$(4)=\"STORE\"",
            16 to " >140 LET A$(5)=\"CABIN\"",
            17 to " >150 PRINT A$(4)",
            18 to " >160 END",
            19 to " >RUN",
            20 to "  STORE",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }
}