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

   /**
    * This program places random color dots in random locations on the screen. In addition, a random sound is generated and
    * played when the dot is placed on the screen.
    */
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

      assertEquals("  * BREAKPOINT AT $breakLineNumber", machine.screen.strings.withoutTrailingBlanks(23, 1))
   }

   /**
    * This program creates an inchworm that moves back and forth across the screen. When the inchworm reaches the edge of the
    * screen, an "uh-oh" sounds, and the inchworm turns around to go in the opposite direction.
    */
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

   /**
    * This program puts a marquee on the screen. The colors are produced randomly, and a tone sounds each time a color bar is
    * placed on the screen.
    */
   @Test
   fun testMarquee() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 REM MARQUEE
         110 RANDOMIZE
         120 CALL CLEAR
         130 FOR S=2 TO 16
         140 CALL COLOR(S,S,S)
         150 NEXT S
         160 CALL HCHAR(7,3,64,28)
         170 CALL HCHAR(16,3,64,28)
         180 CALL VCHAR(7,2,64,10)
         190 CALL VCHAR(7,31,64,10)
         200 FOR A=3 TO 30
         210 GOSUB 310
         220 CALL VCHAR(8,A,C,4)
         230 CALL SOUND(-150,Y,2)
         240 NEXT A
         250 FOR A=30 TO 3 STEP -1
         260 GOSUB 310
         270 CALL VCHAR(12,A,C,4)
         280 CALL SOUND(-150,Y,2)
         290 NEXT A
         300 GOTO 200
         310 C=INT(120*RND)+40
         320 N=INT(24*RND)+1
         330 Y=220*(2^(1/12))^N
         340 RETURN
         """.trimIndent(), machine
      )

      var loops = 0
      val breakLineNumber = 290
      machine.addProgramLineHookAfterLine(breakLineNumber) {
         loops++
         if (loops == 2) throw TiBasicProgramException(breakLineNumber, Breakpoint())
      }
      interpreter.interpret("RUN", machine)

      assertEquals("  * BREAKPOINT AT $breakLineNumber", machine.screen.strings.withoutTrailingBlanks(23, 1))
   }

   /**
    * This program is a secret number game. The object is to guess the randomly chosen number between 1 and an upper limit you
    * input. For each guess, you enter two numbers: a low and a high guess. The computer will tell you if the secret number is
    * less than, greater than, or between the two numbers you enter. When you think you know the number, enter the same value
    * for both the low and high guesses.
    */
   @Test
   fun testSecretNumber() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 REM SECRET NUMBER
         110 RANDOMIZE
         120 MSG1$="SECRET NUMBER IS"
         130 MSG2$="YOUR TWO NUMBERS"
         140 CALL CLEAR
         150 INPUT "ENTER LIMIT? ":LIMIT
         160 SECRET=INT(LIMIT*RND)+1
         170 CALL CLEAR
         180 N=N+1
         190 INPUT "LOW,HIGH GUESSES: ":LOW,HIGH
         200 IF LOW<>HIGH THEN 220
         210 IF SECRET=LOW THEN 300
         220 IF SECRET<LOW THEN 260
         230 IF SECRET>HIGH THEN 280
         240 PRINT MSG1$&" BETWEEN":MSG2$
         250 GOTO 180
         260 PRINT MSG1$&" LESS THAN":MSG2$
         270 GOTO 180
         280 PRINT MSG1$&" LARGER THAN":MSG2$
         290 GOTO 180
         300 PRINT "YOU GUESSED THE SECRET"
         310 PRINT "NUMBER IN ";N;"TRIES"
         320 PRINT "WANT TO PLAY AGAIN?"
         330 INPUT "ENTER Y OR N: ":A$
         340 IF A$<>"Y" THEN 390
         350 N=0
         360 PRINT "WANT TO SET A NEW LIMIT?"
         370 INPUT "ENTER Y OR N: ":B$
         380 IF B$="Y" THEN 140 ELSE 160
         390 END
         """.trimIndent(), machine
      )
      machine.setKeyboardInputProvider(object : KeyboardInputProvider {
         override fun provideInput(ctx: KeyboardInputProvider.InputContext): Sequence<Char> {
            val numberOfGuess = machine.getNumericVariableValue("N").toNative().toInt()
            val secret = machine.getNumericVariableValue("SECRET").toNative().toInt()
            return when (ctx.prompt) {
               "ENTER LIMIT? " -> "100\r"
               "LOW,HIGH GUESSES: " -> if (numberOfGuess == 1) "${secret - 1},${secret + 1}\r" else "$secret,$secret\r"
               "ENTER Y OR N: " -> "N\r"
               else -> error("Don't know how to answer prompt '${ctx.prompt}'")
            }.asSequence()
         }
      })

      interpreter.interpret("RUN", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            13 to "  LOW,HIGH GUESSES: 52,54",
            14 to "  SECRET NUMBER IS BETWEEN",
            15 to "  YOUR TWO NUMBERS",
            16 to "  LOW,HIGH GUESSES: 53,53",
            17 to "  YOU GUESSED THE SECRET",
            18 to "  NUMBER IN  2 TRIES",
            19 to "  WANT TO PLAY AGAIN?",
            20 to "  ENTER Y OR N: N",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

}