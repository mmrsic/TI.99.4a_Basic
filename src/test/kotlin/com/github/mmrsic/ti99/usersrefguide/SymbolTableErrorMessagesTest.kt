package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
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

}