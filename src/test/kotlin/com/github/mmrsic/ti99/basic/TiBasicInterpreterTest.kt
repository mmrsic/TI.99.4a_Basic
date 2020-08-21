package com.github.mmrsic.ti99.basic

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.expr.NumericConstant
import com.github.mmrsic.ti99.hw.KeyboardInputProvider
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test
import kotlin.test.assertEquals

class TiBasicInterpreterTest {

   @Test
   fun testAcceptUserInputParts() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("100 INPUT \"VALUE=\":V", machine)

      var numAcceptCalls = 0
      machine.setKeyboardInputProvider(object : KeyboardInputProvider {
         override fun provideInput(ctx: KeyboardInputProvider.InputContext): Sequence<Char> {
            numAcceptCalls++
            return when (numAcceptCalls) {
               1 -> "123".asSequence()
               else -> "456\r".asSequence()
            }
         }
      })
      interpreter.interpret("RUN", machine)

      TestHelperScreen.assertPrintContents(mapOf(
         16 to "  TI BASIC READY",
         18 to " >100 INPUT \"VALUE=\":V",
         19 to " >RUN",
         20 to "  VALUE=123456",
         22 to "  ** DONE **",
         24 to " >"
      ), machine.screen)
      assertEquals(NumericConstant(123456.0), machine.getNumericVariableValue("V"))
   }
}