package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.basic.expr.NumericConstant
import com.github.mmrsic.ti99.hw.KeyboardInputProvider
import com.github.mmrsic.ti99.hw.StickHorizontalDirection
import com.github.mmrsic.ti99.hw.StickVerticalDirection
import com.github.mmrsic.ti99.hw.TestCaseJoystickInputProvider
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Test case for example found in User's Reference Guide on page II-90.
 */
class CallJoystTest {

   @Test
   fun testMoveBlock() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 CALL CLEAR
            110 CALL CHAR(42,"FFFFFFFFFFFFFFFF")
            120 INPUT "SCREEN COLOR?":S
            130 INPUT "BLOCK COLOR?":F
            140 CALL CLEAR
            150 CALL SCREEN(S)
            160 CALL COLOR(2,F,1)
            170 CALL JOYST(2,X,Y)
            180 A=X*2.2+16.6
            190 B=Y*1.6+12.2
            200 CALL HCHAR(B,A,42)
            210 GOTO 170
            """.trimIndent(), machine
      )
      machine.setKeyboardInputProvider(object : KeyboardInputProvider {
         override fun provideInput(ctx: KeyboardInputProvider.InputContext): Sequence<Char> {
            return when (ctx.prompt) {
               "SCREEN COLOR?" -> "14\r".asSequence()
               "BLOCK COLOR?" -> "9\r".asSequence()
               else -> throw NotImplementedError("Cannot provide input for prompt '${ctx.prompt}'")
            }
         }
      })
      val joystick2 = TestCaseJoystickInputProvider(2)
      machine.plugInJoystick(joystick2)
      var numHcharCalls = 0
      machine.addProgramLineHookAfterLine(200) {
         numHcharCalls++
         if (numHcharCalls >= 10) machine.addBreakpoint(210)
         println(machine.screen.patterns)
      }
      machine.addProgramLineHookAfterLine(210) {
         when (numHcharCalls % 3) {
            1 -> joystick2.horizontalDirection = StickHorizontalDirection.LEFT
            2 -> joystick2.horizontalDirection = StickHorizontalDirection.NONE
            0 -> joystick2.horizontalDirection = StickHorizontalDirection.RIGHT
         }
         when (numHcharCalls / 3) {
            0 -> joystick2.verticalDirection = StickVerticalDirection.UP
            1 -> joystick2.verticalDirection = StickVerticalDirection.NONE
            in 2..3 -> joystick2.verticalDirection = StickVerticalDirection.DOWN
         }
      }
      machine.addProgramLineHookAfterLine(170) {
         val actualX = machine.getNumericVariableValue("X")
         val expectedX = when (joystick2.horizontalDirection) {
            StickHorizontalDirection.NONE -> NumericConstant.ZERO
            StickHorizontalDirection.LEFT -> NumericConstant(-4)
            StickHorizontalDirection.RIGHT -> NumericConstant(4)
         }
         assertEquals(expectedX, actualX, "X value for $joystick2")

         val actualY = machine.getNumericVariableValue("Y")
         val expectedY = when (joystick2.verticalDirection) {
            StickVerticalDirection.NONE -> NumericConstant.ZERO
            StickVerticalDirection.UP -> NumericConstant(4)
            StickVerticalDirection.DOWN -> NumericConstant(-4)
         }
         assertEquals(expectedY, actualY, "Y value for $joystick2")
      }

      interpreter.interpret("RUN", machine)

   }

}