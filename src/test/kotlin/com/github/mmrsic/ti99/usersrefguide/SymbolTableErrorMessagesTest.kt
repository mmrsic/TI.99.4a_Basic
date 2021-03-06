package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Ignore
import org.junit.Test

/**
 * Test cases for errors found when symbol table is generated as described in User's Reference Guide
 * on pages III-8 through III-9.
 */
class SymbolTableErrorMessagesTest {

   /** Test whether a BAD VALUE error appears if a dimension for an array is zero when OPTION BASE = 1. */
   @Test
   fun testOptionBaseOneAndZeroArrayDeclarationIndexGeneratesBadValue() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(listOf("100 OPTION BASE 1", "110 DIM A(0)", "RUN"), machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            16 to "  TI BASIC READY",
            18 to " >100 OPTION BASE 1",
            19 to " >110 DIM A(0)",
            20 to " >RUN",
            22 to "  * BAD VALUE IN 110",
            24 to " >"
         ), machine.screen
      )
   }

   /** Test whether a CAN'T DO THAT error appears if more than one option statement is in a program. */
   @Test
   fun testOptionBaseMoreThanOnceGeneratesCantDoThat() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         1 OPTION BASE 0
         2 OPTION BASE 0
         RUN
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            16 to "  TI BASIC READY",
            18 to " >1 OPTION BASE 0",
            19 to " >2 OPTION BASE 0",
            20 to " >RUN",
            22 to "  * CAN'T DO THAT IN 2",
            24 to " >"
         ), machine.screen
      )
   }

   /** Test whether a CAN'T DO THAT error appears if the OPTION BASE statement is after the first DIM statement. */
   @Test
   fun testOptionBaseLineNumberHigherThanDimensionLineNumberGeneratesCantDoThat() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         1 DIM A(3)
         2 OPTION BASE 1
         3 A(1)=1
         4 A(2)=2
         RUN
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            14 to "  TI BASIC READY",
            16 to " >1 DIM A(3)",
            17 to " >2 OPTION BASE 1",
            18 to " >3 A(1)=1",
            19 to " >4 A(2)=2",
            20 to " >RUN",
            22 to "  * CAN'T DO THAT IN 2",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testForNextError() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         1 FOR I=1 TO 2
         RUN
         2 NEXT I
         RUN
         3 NEXT I
         RUN
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            7 to "  TI BASIC READY",
            9 to " >1 FOR I=1 TO 2",
            10 to " >RUN",
            12 to "  * FOR-NEXT ERROR",
            14 to " >2 NEXT I",
            15 to " >RUN",
            17 to "  ** DONE **",
            19 to " >3 NEXT I",
            20 to " >RUN",
            22 to "  * FOR-NEXT ERROR IN 3",
            24 to " >"
         ), machine.screen
      )
   }

   @Ignore("Not yet functional")
   @Test
   fun testDimStatementNameConflict() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         1 DIM A(9)
         2 DIM A(7)
         RUN
         2 DIM A(7)
         RUN
         2 DIM B(7)
         RUN
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            5 to "  TI BASIC READY",
            7 to " >1 DIM A(9)",
            8 to " >2 DIM A(7)",
            10 to " >RUN",
            12 to "  * NAME CONFLICT IN 2",
            14 to " >2 DIM A(9)",
            15 to " >RUN",
            17 to "  * NAME CONFLICT IN 2",
            19 to " >2 DIM B(7)",
            20 to " >RUN",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

}