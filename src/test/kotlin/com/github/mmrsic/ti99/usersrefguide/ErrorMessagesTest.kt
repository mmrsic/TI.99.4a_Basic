package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for Error Messages explanation found in User's Reference Guide on pages III-8 through III-12.
 */
class ErrorMessagesTest {

   @Test
   fun testBadLineNumberWhenLineNumberIsZeroOrGreaterThan32767() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         0 PRINT "TEST"
         32768 PRINT "TEST"
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            14 to "  TI BASIC READY",
            16 to " >0 PRINT \"TEST\"",
            18 to "  * BAD LINE NUMBER",
            20 to " >32768 PRINT \"TEST\"",
            22 to "  * BAD LINE NUMBER",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testBadLineNumberWhenLineNumberReferencedIsZeroOrGreaterThan32767() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         10 GOTO 0
         20 GOTO 32768
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            14 to "  TI BASIC READY",
            16 to " >10 GOTO 0",
            18 to "  * BAD LINE NUMBER",
            20 to " >20 GOTO 32768",
            22 to "  * BAD LINE NUMBER",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testBadLineNumberWhenResequenceGeneratesLineNumberGreaterThan32767() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         10 REM Test Resequence With Bad Line Number
         20 CALL CLEAR
         30 PRINT
         40 GOTO 10
         RES 8192,8192
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            14 to "  TI BASIC READY",
            16 to " >10 REM Test Resequence With",
            17 to "  Bad Line Number",
            18 to " >20 CALL CLEAR",
            19 to " >30 PRINT",
            20 to " >40 GOTO 10",
            21 to " >RES 8192,8192",
            22 to "  * BAD LINE NUMBER",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testBadNameOnVariableWithTooManyCharacters() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         ABCDEFGHIJKLMNO$="HELLO"
         ABCDEFGHIJKLMN$="HELLO"
         ABCDEFGHIJKLMNO=13
         ABCDEFGHIJKLMNOP=10
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            12 to "  TI BASIC READY",
            14 to " >ABCDEFGHIJKLMNO$=\"HELLO\"",
            15 to "  * BAD NAME",
            17 to " >ABCDEFGHIJKLMN$=\"HELLO\"",
            19 to " >ABCDEFGHIJKLMNO=13",
            21 to " >ABCDEFGHIJKLMNOP=10",
            22 to "  * BAD NAME",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testCantContinue() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         CONTINUE
         100 PRINT "PROGRAM RUN TEST"
         CONTINUE
         RUN
         CONTINUE
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            6 to "  TI BASIC READY",
            8 to " >CONTINUE",
            9 to "  * CAN'T CONTINUE",
            11 to " >100 PRINT \"PROGRAM RUN TEST\"",
            13 to " >CONTINUE",
            14 to "  * CAN'T CONTINUE",
            16 to " >RUN",
            17 to "  PROGRAM RUN TEST",
            19 to "  ** DONE **",
            21 to " >CONTINUE",
            22 to "  * CAN'T CONTINUE",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testCantDoThatWhenStatementIsUsedAsCommand() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         OPTION BASE 1
         DATA 18,0
         DEF PI=3.1428
         FOR I=1 TO 10
         NEXT I
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            2 to "  TI BASIC READY",
            4 to " >OPTION BASE 1",
            6 to "  * CAN'T DO THAT",
            8 to " >DATA 18,0",
            10 to "  * CAN'T DO THAT",
            12 to " >DEF PI=3.1428",
            14 to "  * CAN'T DO THAT",
            16 to " >FOR I=1 TO 10",
            18 to "  * CAN'T DO THAT",
            20 to " >NEXT I",
            22 to "  * CAN'T DO THAT",
            24 to " >"
         ), machine.screen
      )
      interpreter.interpretAll(
         """
         100 REM
         CALL CLEAR
         GOTO 100
         GOSUB 100
         IF 0=0 THEN 100
         INPUT A$
         ON X GOTO 100
         RETURN
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            2 to "  * CAN'T DO THAT",
            4 to " >GOSUB 100",
            6 to "  * CAN'T DO THAT",
            8 to " >IF 0=0 THEN 100",
            10 to "  * CAN'T DO THAT",
            12 to " >INPUT A$",
            14 to "  * CAN'T DO THAT",
            16 to " >ON X GOTO 100",
            18 to "  * CAN'T DO THAT",
            20 to " >RETURN",
            22 to "  * CAN'T DO THAT",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testCantDoThatWhenCommandIsUsedAsStatement() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         1 BYE
         2 NEW
         3 OLD "CS1"
         100 REM
         4 LIST
         5 RUN
         6 SAVE "CS1"
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            1 to "  * CAN'T DO THAT",
            3 to " >2 NEW",
            5 to "  * CAN'T DO THAT",
            7 to " >3 OLD \"CS1\"",
            9 to "  * CAN'T DO THAT",
            11 to " >100 REM",
            12 to " >4 LIST",
            14 to "  * CAN'T DO THAT",
            16 to " >5 RUN",
            18 to "  * CAN'T DO THAT",
            20 to " >6 SAVE \"CS1\"",
            22 to "  * CAN'T DO THAT",
            24 to " >"
         ), machine.screen
      )

      interpreter.interpretAll(
         """
         NEW
         100 BREAK
         RUN
         1 CONTINUE
         1 EDIT 100
         1 NUMBER 1000
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            6 to "  TI BASIC READY",
            8 to " >100 BREAK",
            9 to " >RUN",
            11 to "  * BREAKPOINT AT 100",
            12 to " >1 CONTINUE",
            14 to "  * CAN'T DO THAT",
            16 to " >1 EDIT 100",
            18 to "  * CAN'T DO THAT",
            20 to " >1 NUMBER 1000",
            22 to "  * CAN'T DO THAT",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testCantDoThatWhenNoProgramIsPresent() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         LIST
         RUN
         SAVE "CS1"
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            10 to "  TI BASIC READY",
            12 to " >LIST",
            14 to "  * CAN'T DO THAT",
            16 to " >RUN",
            18 to "  * CAN'T DO THAT",
            20 to " >SAVE \"CS1\"",
            22 to "  * CAN'T DO THAT",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testIncorrectStatementForVariables() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(listOf("ABC A", "A\$A", "N 257", "PRINT \""), machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            10 to "  TI BASIC READY",
            12 to " >ABC A",
            13 to "  * INCORRECT STATEMENT",
            15 to " >A\$A",
            16 to "  * INCORRECT STATEMENT",
            18 to " >N 257",
            19 to "  * INCORRECT STATEMENT",
            21 to " >PRINT \"",
            22 to "  * INCORRECT STATEMENT",
            24 to " >"
         ), machine.screen
      )
   }

}