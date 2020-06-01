package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for ASCII character codes table found in User's Reference Guide on page
 */
class AsciiCharacterCodesTest {

   @Test
   fun testAppendixTable() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 CALL CLEAR
         110 FOR I=32 TO 126
         120 PRINT CHR$(I);
         130 NEXT I
         RUN
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            18 to "   !\"#$%&'()*+,-./0123456789:;",
            19 to "  <=>?@ABCDEFGHIJKLMNOPQRSTUVW",
            20 to "  XYZ[\\]^_`abcdefghijklmnopqrs",
            21 to "  tuvwxyz{|}~",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

}