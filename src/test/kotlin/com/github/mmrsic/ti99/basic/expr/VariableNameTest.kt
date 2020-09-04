package com.github.mmrsic.ti99.basic.expr

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for TI BASIC variable names.
 */
class VariableNameTest {

   /** Test whether the names of the CALL subprograms may be used as variable names. */
   @Test
   fun testSubprogramNames() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         CHAR=1
         CLEAR=2
         COLOR=3
         GCHAR=4
         HCHAR=5
         JOYST=6
         KEY=7
         SCREEN=8
         SOUND=9
         VCHAR=10
         PRINT CHAR;CLEAR;COLOR;GCHAR;HCHAR;JOYST;KEY;SCREEN;SOUND;VCHAR
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            2 to " >COLOR=3",
            4 to " >GCHAR=4",
            6 to " >HCHAR=5",
            8 to " >JOYST=6",
            10 to " >KEY=7",
            12 to " >SCREEN=8",
            14 to " >SOUND=9",
            16 to " >VCHAR=10",
            18 to " >PRINT CHAR;CLEAR;COLOR;GCHAR",
            19 to "  ;HCHAR;JOYST;KEY;SCREEN;SOUN",
            20 to "  D;VCHAR",
            21 to "   1  2  3  4  5  6  7  8  9",
            22 to "   10",
            24 to " >"
         ), machine.screen
      )
   }
}