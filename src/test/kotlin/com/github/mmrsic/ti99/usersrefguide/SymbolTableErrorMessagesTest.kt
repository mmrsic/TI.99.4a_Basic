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

   /** Test whether a BAD VALUE is thrown if a dimension for an array is zero when OPTION BASE = 1. */
   @Test
   fun test() {
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
}