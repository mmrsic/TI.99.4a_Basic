package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.Breakpoint
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.basic.TiBasicProgramException
import com.github.mmrsic.ti99.hw.KeyboardInputProvider
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Test cases for example programs found in User's Reference Guide on pages III-14 through III-34.
 */
class ApplicationProgramsTest {

   @Test
   fun testRandomColorDots() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 REM RANDOM COLOR DOTS
         110 RANDOMIZE
         120 CALL CLEAR
         130 FOR C=2 TO 16
         140 CALL COLOR(C,C,C)
         150 NEXT C
         160 N=INT(24*RND)+1
         170 Y=110*(2^(1/12))^N
         180 CHAR=INT(120*RND)+40
         190 ROW=INT(24*RND)+1
         200 COL=INT(32*RND)+1
         210 CALL SOUND(-500,Y,2)
         220 CALL HCHAR(ROW,COL,CHAR)
         230 GOTO 160
         """.trimIndent(), machine
      )
      var loops = 0
      val breakLineNumber = 230
      machine.addProgramLineHookAfterLine(breakLineNumber) {
         loops++
         if (loops == 10) throw TiBasicProgramException(breakLineNumber, Breakpoint())
      }
      interpreter.interpret("RUN", machine)

      assertEquals("  * BREAKPOINT AT 230", machine.screen.strings.withoutTrailingBlanks(23, 1))
   }

   @Test
   fun testInchworm() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 REM INCHWORM
         110 CALL CLEAR
         120 INPUT "COLOR? ":C
         130 CALL CLEAR
         140 CALL COLOR(2,C,C)
         150 XOLD=1
         160 XDIR=1
         170 FOR I=1 TO 31
         180 XNEW=XOLD+XDIR
         190 CALL HCHAR(12,XNEW,42)
         200 FOR DELAY=1 TO 200
         210 NEXT DELAY
         220 CALL HCHAR(12,XOLD,32)
         230 XOLD=XNEW
         240 NEXT I
         250 XDIR=-XDIR
         260 CALL SOUND(100,392,2)
         270 CALL SOUND(100,330,2)
         280 GOTO 170
         """.trimIndent(), machine
      )
      machine.setKeyboardInputProvider(object : KeyboardInputProvider {
         override fun provideInput(ctx: KeyboardInputProvider.InputContext): Sequence<Char> {
            return "7\r".asSequence()
         }
      })

      val breakLineNumber = 270
      machine.addProgramLineHookAfterLine(breakLineNumber) {
         if (machine.getNumericVariableValue("XDIR").constant.toInt() > 0) {
            throw TiBasicProgramException(breakLineNumber, Breakpoint())
         }
      }
      interpreter.interpret("RUN", machine)
      TestHelperScreen.assertPrintContents(
         mapOf(
            10 to "*",
            23 to "  * BREAKPOINT AT $breakLineNumber",
            24 to " >"
         ), machine.screen
      )
   }

}