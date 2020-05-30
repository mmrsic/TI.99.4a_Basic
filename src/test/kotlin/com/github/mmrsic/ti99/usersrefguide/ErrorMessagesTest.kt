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

}