package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases found in User Reference Guide in section Relational Expressions on page II-14.
 */
class RelationalExpressionsTest {

   @Test
   fun testUsedAsNumericExpressions() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("100 A=2<5", machine)
      interpreter.interpret("110 B=3<=2", machine)
      interpreter.interpret("120 PRINT A;B", machine)
      interpreter.interpret("RUN", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            14 to "  TI BASIC READY",
            16 to " >100 A=2<5",
            17 to " >110 B=3<=2",
            18 to " >120 PRINT A;B",
            19 to " >RUN",
            20 to "  -1  0",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testStringConcatenationNotEquals() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("100 A$=\"HI\"", machine)
      interpreter.interpret("110 B$=\" THERE!\"", machine)
      interpreter.interpret("120 PRINT (A$&B$)=\"HI!\"", machine)
      interpreter.interpret("RUN", machine)
      interpreter.interpret("120 PRINT (A$&B$)>\"HI\"", machine)
      interpreter.interpret("RUN", machine)
      interpreter.interpret("120 PRINT (A$>B$)*4", machine)
      interpreter.interpret("RUN", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            2 to "  TI BASIC READY",
            4 to " >100 A$=\"HI\"",
            5 to " >110 B$=\" THERE!\"",
            6 to " >120 PRINT (A$&B$)=\"HI!\"",
            7 to " >RUN",
            8 to "   0",
            10 to "  ** DONE **",
            12 to " >120 PRINT (A$&B$)>\"HI\"",
            13 to " >RUN",
            14 to "  -1",
            16 to "  ** DONE **",
            18 to " >120 PRINT (A$>B$)*4",
            19 to " >RUN",
            20 to "  -4",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testStringConcatenationEquals() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("100 A$=\"HI\"", machine)
      interpreter.interpret("110 B$=\" THERE\"", machine)
      interpreter.interpret("120 PRINT \"HI THERE\"=(A$&B$)", machine)
      interpreter.interpret("RUN", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            13 to "  TI BASIC READY",
            15 to " >100 A$=\"HI\"",
            16 to " >110 B$=\" THERE\"",
            17 to " >120 PRINT \"HI THERE\"=(A$&B$)",
            19 to " >RUN",
            20 to "  -1",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testAdditionalUsagesAsNumericExpressions() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("100 A=2<4*3", machine)
      interpreter.interpret("110 B=A=0", machine)
      interpreter.interpret("120 PRINT A;B", machine)
      interpreter.interpret("RUN", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            14 to "  TI BASIC READY",
            16 to " >100 A=2<4*3",
            17 to " >110 B=A=0",
            18 to " >120 PRINT A;B",
            19 to " >RUN",
            20 to "  -1  0",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )

   }

}