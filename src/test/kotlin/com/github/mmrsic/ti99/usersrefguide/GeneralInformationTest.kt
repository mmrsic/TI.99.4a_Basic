package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Ignore
import org.junit.Test

/**
 * Test cases found in User Reference Guide in section General Information on pages II-4 to II-7.
 */
class GeneralInformationTest {

   @Test
   fun testExample() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)

      interpreter.interpret("NEW", machine)

      TestHelperScreen.assertPrintContents(mapOf(22 to "  TI BASIC READY", 24 to " >"), machine.screen)
      TestHelperScreen.assertCursorAt(24, 3, machine.screen)

      interpreter.interpret("10 A=2", machine)
      interpreter.interpret("RUN", machine)
      interpreter.interpret("PRINT A", machine)
      interpreter.interpret("20 B=3", machine)
      interpreter.interpret("PRINT A", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            10 to "  TI BASIC READY",
            12 to " >10 A=2",
            13 to " >RUN",
            15 to "  ** DONE **",
            17 to " >PRINT A",
            18 to "   2",
            20 to " >20 B=3",
            21 to " >PRINT A",
            22 to "   0",
            24 to " >"
         ), machine.screen
      )
      TestHelperScreen.assertCursorAt(24, 3, machine.screen)
   }

   @Ignore("Not yet implemented")
   @Test
   fun testSpaceInLineNumber() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)

      interpreter.interpret("1 00 PRINT \"HELLO\"", machine)
      interpreter.interpret("LIST", machine)
      interpreter.interpret("RUN", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            14 to "  TI BASIC READY",
            16 to " >1 00 PRINT \"HELLO\"",
            17 to " >LIST",
            18 to "  1 00 PRINT \"HELLO\"",
            19 to " >RUN",
            21 to "  * INCORRECT STATEMENT",
            22 to "     IN 1",
            24 to " >"
         ), machine.screen
      )
      TestHelperScreen.assertCursorAt(24, 3, machine.screen)
   }

   @Test
   fun testSpaceWithinReservedWord() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)

      interpreter.interpret("110 PR INT \"HOW ARE YOU?\"", machine)
      interpreter.interpret("LIST", machine)
      interpreter.interpret("RUN", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            14 to "  TI BASIC READY",
            16 to " >100 PR INT \"HOW ARE YOU?\"",
            17 to " >LIST",
            18 to "  100 PR INT\"HOW ARE YOU?\"", // No space after PR INT
            19 to " >RUN",
            21 to "  * INCORRECT STATEMENT",
            22 to "     IN 110",
            24 to " >"
         ), machine.screen
      )
      TestHelperScreen.assertCursorAt(24, 3, machine.screen)
   }

   @Test
   fun testSpaceWithinNumericConstant() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)

      interpreter.interpret("120 LET A=1 00", machine)
      interpreter.interpret("LIST", machine)
      interpreter.interpret("RUN", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            11 to "  TI BASIC READY",
            13 to " >120 LET A=1 00",
            14 to "  * INCORRECT STATEMENT",
            16 to " >LIST",
            18 to "  * CAN'T DO THAT",
            20 to " >RUN",
            22 to "  * CAN'T DO THAT",
            24 to " >"
         ), machine.screen
      )
      TestHelperScreen.assertCursorAt(24, 3, machine.screen)
   }

   @Test
   fun testSpaceWithinVariableName() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)

      interpreter.interpret("130 LET CO ST=24.95", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            19 to "  TI BASIC READY",
            21 to " >130 LET CO ST=24.95",
            22 to "  * INCORRECT STATEMENT",
            24 to " >"
         ), machine.screen
      )
      TestHelperScreen.assertCursorAt(24, 3, machine.screen)
   }

}