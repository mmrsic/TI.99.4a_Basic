package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Some commands in TI BASIC may be entered as part of a program. Generally, the commands work the same
 * way when they are used as statement. The following commands may be used as statements:
 *
 * BREAK, UNBREAK, TRACE, UNTRACE, DELETE
 *
 * See User Reference Guide in section Commands Used as Statements on page II-18.
 */
class CommandsUsedAsStatementsTest {

   @Test
   fun testSimpleCommands() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 BREAK
         110 UNBREAK
         120 TRACE
         130 UNTRACE
         140 DELETE "DSK2.FILE"
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            17 to "  TI BASIC READY",
            19 to " >100 BREAK",
            20 to " >110 UNBREAK",
            21 to " >120 TRACE",
            22 to " >130 UNTRACE",
            23 to " >140 DELETE \"DSK2.FILE\"",
            24 to " >"
         ), machine.screen
      )
   }
}