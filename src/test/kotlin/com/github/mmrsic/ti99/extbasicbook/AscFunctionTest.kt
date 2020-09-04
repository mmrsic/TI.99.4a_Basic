package com.github.mmrsic.ti99.extbasicbook

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * _ASC(string-expression)_
 *
 * The ASC function gives the ASCII character code which corresponds to the first character of string-expression. A list of
 * the ASCII code is given in Appendix C. The ASC function is the inverse of the CHR$ function.
 */
class AscFunctionTest {

   /** PRINT ASC("A") prints 65. */
   @Test
   fun testPrinting() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 PRINT ASC("A")
         RUN
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            16 to "  TI BASIC READY",
            18 to " >100 PRINT ASC(\"A\")",
            19 to " >RUN",
            20 to "   65",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   /** B=ASC("1") sets B equal to 49. */
   @Test
   fun testAssignment() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 B=ASC("1")
         RUN
         PRINT B
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            14 to "  TI BASIC READY",
            16 to " >100 B=ASC(\"1\")",
            17 to " >RUN",
            19 to "  ** DONE **",
            21 to " >PRINT B",
            22 to "   49",
            24 to " >"
         ), machine.screen
      )
   }

   /** DISPLAY ASC("HELLO") displays 72. */
   @Test
   fun testDisplayAsciiValueOfFirstCharacterOfString() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 DISPLAY ASC("HELLO")
         RUN
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            16 to "  TI BASIC READY",
            18 to " >100 DISPLAY ASC(\"HELLO\")",
            19 to " >RUN",
            20 to "   72",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }
}