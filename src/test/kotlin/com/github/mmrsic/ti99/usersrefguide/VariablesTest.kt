package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Ignore
import org.junit.Test

/**
 * Test cases found in User Reference Guide in section Variables on page II-11.
 */
class VariablesTest {

   @Test
   fun testBadNameErrorWhenVariableTooLong() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("110 ABCDEFGHIJKLMNOPQ=3", machine)
      interpreter.interpret("ABCDEFGHIJKLMNOPQ=13", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            16 to "  TI BASIC READY",
            18 to " >110 ABCDEFGHIJKLMNOPQ=3",
            19 to "  * BAD NAME",
            21 to " >ABCDEFGHIJKLMNOPQ=13",
            22 to "  * BAD NAME",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testAtSignAsVariableName() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("@=1", machine)
      interpreter.interpret("PRINT @", machine)
      interpreter.interpret("PRINT @-2", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            14 to "  TI BASIC READY",
            16 to " >@=1",
            18 to " >PRINT @",
            19 to "   1",
            21 to " >PRINT @-2",
            22 to "  -1",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testLeftBracketInVariableName() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("[X]=2", machine)
      interpreter.interpret("[Y]=3", machine)
      interpreter.interpret("PRINT [X] * [Y]", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            15 to "  TI BASIC READY",
            17 to " >[X]=2",
            19 to " >[Y]=3",
            21 to " >PRINT [X] * [Y]",
            22 to "   6",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testRightBracketInVariableName() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("]I[ = 7", machine)
      interpreter.interpret("PRINT 10* ]I[", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            17 to "  TI BASIC READY",
            19 to " >]I[ = 7",
            21 to " >PRINT 10* ]I[",
            22 to "   70",
            24 to " >"
         ), machine.screen
      )
      TestHelperScreen.assertCursorAt(24, 3, machine.screen)
   }

   @Test
   fun testBackslashInVariableName() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("\\VARIABLE\\ = -1", machine)
      interpreter.interpret("PRINT \\VARIABLE\\", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            17 to "  TI BASIC READY",
            19 to " >\\VARIABLE\\ = -1",
            21 to " >PRINT \\VARIABLE\\",
            22 to "  -1",
            24 to " >"
         ), machine.screen
      )
      TestHelperScreen.assertCursorAt(24, 3, machine.screen)
   }

   @Test
   fun testUnderlineInVariableName() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("___=4", machine)
      interpreter.interpret("PRINT 4*___", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            17 to "  TI BASIC READY",
            19 to " >___=4",
            21 to " >PRINT 4*___",
            22 to "   16",
            24 to " >"
         ), machine.screen
      )
      TestHelperScreen.assertCursorAt(24, 3, machine.screen)
   }

   @Test
   fun testDigitsInVariableName() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("X1=1", machine)
      interpreter.interpret("PRINT X1 / 5", machine)
      interpreter.interpret("PRINT X4", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            14 to "  TI BASIC READY",
            16 to " >X1=1",
            18 to " >PRINT X1 / 5",
            19 to "   .2",
            21 to " >PRINT X4",
            22 to "   0",
            24 to " >"
         ), machine.screen
      )
      TestHelperScreen.assertCursorAt(24, 3, machine.screen)
   }

   @Test
   fun testMaxLenVariableNames() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("VARIABLE9012345=6", machine)
      interpreter.interpret("VARIABLE901234$=\"FIVE\"", machine)
      interpreter.interpret("PRINT VARIABLE9012345;VARIABLE901234$", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            14 to "  TI BASIC READY",
            16 to " >VARIABLE9012345=6",
            18 to " >VARIABLE901234$=\"FIVE\"",
            20 to " >PRINT VARIABLE9012345;VARIAB",
            21 to "  LE901234$",
            22 to "   6 FIVE",
            24 to " >"
         ), machine.screen
      )
      TestHelperScreen.assertCursorAt(24, 3, machine.screen)
   }

   @Ignore("Not yet functional: LIST ist not parsed when followed by equals sign")
   @Test
   fun testListIsNotAllowedAsNumericVariable_noProgram() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("LIST=1", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            18 to "  TI BASIC READY",
            20 to " >LIST=1",
            22 to "  * CAN'T DO THAT",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testListIsNotAllowedAsNumericVariable_programPresent() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("10 REM TEST LIST", machine)
      interpreter.interpret("LIST=1", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            18 to "  TI BASIC READY",
            20 to " >10 REM TEST LIST",
            21 to " >LIST=1",
            22 to "  * INCORRECT STATEMENT",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testListIsAllowedAsStringVariable() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("LIST$=\"1\"", machine)
      interpreter.interpret("PRINT LIST$", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            17 to "  TI BASIC READY",
            19 to " >LIST$=\"1\"",
            21 to " >PRINT LIST$",
            22 to "  1",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testStringsAreLimitedTo255Characters() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("TEN$=\"1234567890\"", machine)
      interpreter.interpret("FIFTY$=TEN$&TEN$&TEN$&TEN$&TEN$", machine)
      interpreter.interpret("THREEHUNDRED$=FIFTY$&FIFTY$&FIFTY$&FIFTY$&FIFTY$&FIFTY$", machine)
      interpreter.interpret("PRINT FIFTY$", machine)
      interpreter.interpret("PRINT THREEHUNDRED$", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            2 to " >FIFTY$=TEN$&TEN$&TEN$&TEN$&T",
            3 to "  EN$",
            5 to " >THREEHUNDRED$=FIFTY$&FIFTY$&",
            6 to "  FIFTY$&FIFTY$&FIFTY$&FIFTY$",
            8 to " >PRINT FIFTY$",
            9 to "  1234567890123456789012345678",
            10 to "  9012345678901234567890",
            12 to " >PRINT THREEHUNDRED$",
            13 to "  1234567890123456789012345678",
            14 to "  9012345678901234567890123456",
            15 to "  7890123456789012345678901234",
            16 to "  5678901234567890123456789012",
            17 to "  3456789012345678901234567890",
            18 to "  1234567890123456789012345678",
            19 to "  9012345678901234567890123456",
            20 to "  7890123456789012345678901234",
            21 to "  5678901234567890123456789012",
            22 to "  345",
            24 to " >"
         ), machine.screen
      )
   }

}