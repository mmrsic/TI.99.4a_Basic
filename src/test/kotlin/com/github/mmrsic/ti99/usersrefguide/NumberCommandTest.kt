package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * When the NUMBER command is entered, your computer automatically generates line numbers for you. Your
 * computer is in Number Mode when it is generating line numbers. In Number Mode each line entered in
 * response to a generated line number is added to the program.
 * The first line displayed after entering the NUMBER command is the specified initial-line. Succeeding
 * line numbers are generated using the specified increment.
 * If no initial-line and no increment are specified, then 100 is used as the initial-line and 10 is used
 * as the increment. If you specify only an initial-line, then 10 is used as the increment. If you specify
 * just an increment, then 100 is used as the initial-line.
 */
class NumberCommandTest {

   @Test
   fun testNumberTenFive() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("NUMBER 10,5", machine)
   }

   @Test
   fun testNum() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("NUM", machine)
   }

   @Test
   fun testNumberFifty() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("NUMBER 50", machine)
   }

   @Test
   fun testNumComma5() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("NUM ,5", machine)
   }

}