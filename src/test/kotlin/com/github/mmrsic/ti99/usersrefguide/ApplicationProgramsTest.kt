package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.Breakpoint
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.basic.TiBasicProgramException
import com.github.mmrsic.ti99.basic.expr.NumericConstant
import com.github.mmrsic.ti99.hw.KeyboardInputProvider
import com.github.mmrsic.ti99.hw.TiBasicModule
import com.github.mmrsic.ti99.hw.TiCode
import com.github.mmrsic.ti99.hw.TiColor
import com.github.mmrsic.ti99.hw.TiPlainCode
import com.github.mmrsic.ti99.hw.ti994aKeyForCode
import com.github.mmrsic.ti99.hw.toCode
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

   /**
    * This program moves a ball and bounces it off the edges of he screen. Each time the ball hits any side, a tone sounds,
    * and the ball is deflected.
    */
   @Test
   fun testBouncingBall() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 REM BOUNCING BALL
         110 CALL CLEAR
         120 CALL CHAR(96,"3C7EFFFFFFFF7E3C")
         130 INPUT "BALL COLOR? ":C
         140 INPUT "SCREEN COLOR? ":S
         150 CALL CLEAR
         160 CALL COLOR(9,C,S)
         170 CALL COLOR(1,S,S)
         180 X=16
         190 Y=12
         200 XDIR=1
         210 YDIR=1
         220 X=X+XDIR
         230 Y=Y+YDIR
         240 IF X<1 THEN 310
         250 IF X>32 THEN 310
         260 IF Y<1 THEN 360
         270 IF Y>24 THEN 360
         280 CALL CLEAR
         290 CALL HCHAR(Y,X,96)
         300 GOTO 220
         310 XDIR=-XDIR
         320 CALL SOUND(30,380,2)
         330 IF Y<1 THEN 360
         340 IF Y>24 THEN 360
         350 GOTO 220
         360 YDIR=-YDIR
         370 CALL SOUND(30,380,2)
         380 GOTO 220
         """.trimIndent(), machine
      )
      machine.setKeyboardInputProvider(object : KeyboardInputProvider {
         override fun provideInput(ctx: KeyboardInputProvider.InputContext): Sequence<Char> {
            return when (ctx.prompt) {
               "BALL COLOR? " -> TiColor.MediumRed.toCode().toString() + "\r"
               "SCREEN COLOR? " -> TiColor.Gray.toCode().toString() + "\r"
               else -> error("Don't know what input to provide for prompt '${ctx.prompt}' at line ${ctx.programLine}")
            }.asSequence()
         }
      })

      val breakpointLine = 230
      var steps = 1
      machine.addProgramLineHookAfterLine(breakpointLine) {
         if (steps == 100) throw TiBasicProgramException(breakpointLine, Breakpoint())
         steps++
      }
      interpreter.interpret("RUN", machine)

      assertEquals(mapOf(
         "X" to NumericConstant(16.0),
         "Y" to NumericConstant(12.0),
         "XDIR" to NumericConstant(-1.0),
         "YDIR" to NumericConstant(1.0),
         "C" to NumericConstant(TiColor.MediumRed.toCode().toDouble()),
         "S" to NumericConstant(TiColor.Gray.toCode().toDouble())
      ).toSortedMap(), machine.getAllNumericVariableValues().toSortedMap(), "Numeric variable values")
   }

   /**
    * On each month all of us have the opportunity to tackle "balancing" our checkbooks against our bank statements. Normally,
    * the checkbook balance will not agree with the balance shown on the bank statement because there are checks and deposits
    * that haven't cleared yet. This program will help you balance your checkbook quickly and easily.
    */
   @Test
   fun testCheckbookBalance() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 REM CHECKBOOK BALANCE
         110 CALL CLEAR
         120 INPUT "BANK BALANCE? ":BALANCE
         130 DISPLAY "ENTER EACH OUTSTANDING"
         140 DISPLAY "CHECK NUMBER AND AMOUNT."
         150 DISPLAY
         160 DISPLAY "ENTER A ZERO FOR THE"
         170 DISPLAY "CHECK NUMBER WHEN FINISHED."
         180 DISPLAY
         190 N=N+1
         200 INPUT "CHECK NUMBER? ":CNUM(N)
         210 IF CNUM(N)=0 THEN 250
         220 INPUT "CHECK AMOUNT? ":CAMT(N)
         230 CTOTAL=CTOTAL+CAMT(N)
         240 GOTO 190
         250 DISPLAY "ENTER EACH OUTSTANDING"
         260 DISPLAY "DEPOSIT AMOUNT."
         270 DISPLAY
         280 DISPLAY "ENTER A ZERO AMOUNT"
         290 DISPLAY "WHEN FINISHED."
         300 DISPLAY
         310 M=M+1
         320 INPUT "DEPOSIT AMOUNT? ":DAMT(M)
         330 IF DAMT(M)=0 THEN 360
         340 DTOTAL=DTOTAL+DAMT(M)
         350 GOTO 310
         360 NBAL=BALANCE-CTOTAL+DTOTAL
         370 DISPLAY "NEW BALANCE= ";NBAL
         380 INPUT "CHECKBOOK BALANCE? ":CBAL
         390 DISPLAY "CORRECTION= ";NBAL-CBAL
         400 END
         """.trimIndent(), machine
      )
      assertEquals((100..400 step 10).toList(), machine.program!!.listing.map { it.lineNumber }, "Program line numbers")

      machine.setKeyboardInputProvider(object : KeyboardInputProvider {
         override fun provideInput(ctx: KeyboardInputProvider.InputContext): Sequence<Char> {
            return (when (ctx.prompt) {
               "BANK BALANCE? " -> "940.26"
               "CHECK NUMBER? " -> when (ctx.programLineCalls) {
                  1 -> "212"
                  2 -> "213"
                  3 -> "216"
                  4 -> "218"
                  5 -> "219"
                  6 -> "220"
                  else -> "0"
               }
               "CHECK AMOUNT? " -> when (ctx.programLineCalls) {
                  1 -> "76.83"
                  2 -> "122.87"
                  3 -> "219.50"
                  4 -> "397.31"
                  5 -> "231.00"
                  6 -> "138.25"
                  else -> error("Unable to provide input at line ${ctx.programLine} (call no. ${ctx.programLineCalls})")
               }
               "DEPOSIT AMOUNT? " -> if (ctx.programLineCalls == 1) "450" else "0"
               "CHECKBOOK BALANCE? " -> "209.15"
               else -> error("Unable to provide input for prompt '${ctx.prompt}'")
            } + '\r').asSequence()
         }
      })
      interpreter.interpret("RUN", machine)
      TestHelperScreen.assertPrintContents(
         mapOf(
            1 to "  CHECK NUMBER? 216",
            2 to "  CHECK AMOUNT? 219.50",
            3 to "  CHECK NUMBER? 218",
            4 to "  CHECK AMOUNT? 397.31",
            5 to "  CHECK NUMBER? 219",
            6 to "  CHECK AMOUNT? 231.00",
            7 to "  CHECK NUMBER? 220",
            8 to "  CHECK AMOUNT? 138.25",
            9 to "  CHECK NUMBER? 0",
            10 to "  ENTER EACH OUTSTANDING",
            11 to "  DEPOSIT AMOUNT.",
            13 to "  ENTER A ZERO AMOUNT",
            14 to "  WHEN FINISHED.",
            16 to "  DEPOSIT AMOUNT? 450",
            17 to "  DEPOSIT AMOUNT? 0",
            18 to "  NEW BALANCE=  204.5",
            19 to "  CHECKBOOK BALANCE? 209.15",
            20 to "  CORRECTION= -4.65",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   /**
    * Codebreaker is a game in which the computer generates a four-digit code number, and you try to guess it. Zeros are not
    * allowed, and no two digits may be the same.
    */
   @Test
   fun testCodebreakerGame() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 REM CODEBREAKER GAME
         110 RANDOMIZE
         120 CALL CLEAR
         130 FOR I=1 TO 4
         140 N(I) = INT(9*RND)+1
         150 IF I=1 THEN 190
         160 FOR J=1 TO I-1
         170 IF N(I)=N(J) THEN 140
         180 NEXT J
         190 NEXT I
         200 TRIES = 0
         210 INPUT "ENTER GUESS? ":GUESS
         220 SCORE = 0
         230 TRIES = TRIES+1
         240 FOR K=4 TO 1 STEP -1
         250 DIGIT = (GUESS/10-INT(GUESS/10))*10
         260 IF DIGIT<>N(K) THEN 290
         270 SCORE=SCORE+1
         280 GOTO 340
         290 FOR L=1 TO 4
         300 IF N(L)<>DIGIT THEN 330
         310 SCORE = SCORE+.1
         320 GOTO 340
         330 NEXT L
         340 GUESS = INT(GUESS/10)
         350 NEXT K
         360 IF INT(SCORE)<>SCORE THEN 390
         370 PRINT STR$(SCORE)&".0"
         380 GOTO 430
         390 IF SCORE>1 THEN 420
         400 PRINT "0"&STR$(SCORE)
         410 GOTO 430
         420 PRINT STR$(SCORE)
         430 IF SCORE<>4 THEN 210
         440 PRINT "YOU TOOK "&STR$(TRIES)&" TRIES TO GUESS"
         450 PRINT "THE CODE NUMBER."
         460 DISPLAY "WOULD YOU LIKE TO PLAY AGAIN"
         470 INPUT "ENTER Y OR N: ":A$
         480 IF A$="Y" THEN 110
         490 END
         """.trimIndent(), machine
      )

      assertEquals((100..490 step 10).toList(), machine.program!!.listing.map { it.lineNumber }, "Program line numbers")
      machine.addProgramLineHookAfterLine(200) {
         val oldCode = with(StringBuilder()) {
            (1..4).forEach { idx ->
               append(machine.getNumericArrayVariableValue("N", listOf(NumericConstant(idx))).constant.toInt())
            }
            toString()
         }
         val newCode = "5718"
         println("Changing code in order for test case: New code:$newCode, old code: $oldCode ")
         newCode.withIndex().forEach {
            machine.setNumericArrayVariable("N", listOf(NumericConstant(it.index + 1)), NumericConstant(it.value.toInt() - 48))
         }
      }

      machine.setKeyboardInputProvider(object : KeyboardInputProvider {
         override fun provideInput(ctx: KeyboardInputProvider.InputContext): Sequence<Char> {
            return (when (ctx.prompt) {
               "ENTER GUESS? " -> when (ctx.programLineCalls) {
                  1 -> "1234"
                  2 -> "5678"
                  3 -> "9238"
                  4 -> "5694"
                  5 -> "5198"
                  6 -> "5718"
                  else -> throw TiBasicProgramException(ctx.programLine, Breakpoint())
               }
               "ENTER Y OR N: " -> "N"
               else -> error("Unable to provide input for prompt '${ctx.prompt}'")
            } + '\r').asSequence()
         }
      })
      interpreter.interpret("RUN", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            5 to "  ENTER GUESS? 1234",
            6 to "  0.1",
            7 to "  ENTER GUESS? 5678",
            8 to "  2.1",
            9 to "  ENTER GUESS? 9238",
            10 to "  1.0",
            11 to "  ENTER GUESS? 5694",
            12 to "  1.0",
            13 to "  ENTER GUESS? 5198",
            14 to "  2.1",
            15 to "  ENTER GUESS? 5718",
            16 to "  4.0",
            17 to "  YOU TOOK 6 TRIES TO GUESS",
            18 to "  THE CODE NUMBER.",
            19 to "  WOULD YOU LIKE TO PLAY AGAIN",
            20 to "  ENTER Y OR N: N",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   /** Test case for example found on pages III-26 and III-27 of User's Reference Guide. */
   @Test
   fun testCharacterDefinition() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 REM CHARACTER DEFINITION
         110 DIM B(8,8)
         120 CALL CHAR(100,"")
         130 CALL CHAR(101,"FFFFFFFFFFFFFFFF")
         140 CALL COLOR(9,2,16)
         150 CALL CLEAR
         160 M$="AUTO CHARACTER DEFINITION"
         170 Y=3
         180 X=4
         190 GOSUB 770
         200 M$="12345678"
         210 Y=8
         220 GOSUB 770
         230 GOSUB 820
         240 M$="0=OFF=WHITE"
         250 Y=22
         260 X=4
         270 GOSUB 770
         280 M$="1=ON=BLACK"
         290 Y=23
         300 GOSUB 770
         310 FOR R=1 TO 8
         320 CALL HCHAR(8+R,5,100,8)
         330 NEXT R
         340 FOR R=1 TO 8
         350 FOR C=1 TO 8
         360 CALL HCHAR(8+R,4+C,30)
         370 CALL KEY(0,KEY,STATUS)
         380 IF STATUS=0 THEN 370
         390 IF (KEY<>8)+(KEY<>9)=-2 THEN 420
         400 GOSUB 870
         410 GOTO 360
         420 KEY=KEY-48
         430 IF (KEY<0)+(KEY>1)<=-1 THEN 370
         440 B(R,C)=KEY
         450 CALL HCHAR(8+R,4+C,100+KEY)
         460 NEXT C
         470 NEXT R
         480 HEX$="0123456789ABCDEF"
         490 M$=""
         500 FOR R=1 TO 8
         510 LOW=B(R,5)*8+B(R,6)*4+B(R,7)*2+B(R,8)+1
         520 HIGH=B(R,1)*8+B(R,2)*4+B(R,3)*2+B(R,4)+1
         530 M$=M$&SEG$(HEX$,HIGH,1)&SEG$(HEX$,LOW,1)
         540 NEXT R
         550 CALL CHAR(102,M$)
         560 CALL HCHAR(8,20,102)
         570 FOR R=0 TO 2
         580 CALL HCHAR(12+R,20,102,3)
         590 NEXT R
         600 Y=16
         610 X=12
         620 GOSUB 770
         630 M$="PRESS Q TO QUIT"
         640 Y=18
         650 X=12
         660 GOSUB 770
         670 M$="PRESS ANY OTHER"
         680 Y=19
         690 GOSUB 770
         700 M$="KEY TO CONTINUE"
         710 Y=20
         720 GOSUB 770
         730 CALL KEY(0,KEY,STATUS)
         740 IF STATUS=0 THEN 730
         750 IF KEY<>81 THEN 140
         760 STOP
         770 FOR I=1 TO LEN(M$)
         780 CODE=ASC(SEG$(M$,I,1))
         790 CALL HCHAR(Y,X+I,CODE)
         800 NEXT I
         810 RETURN
         820 FOR I=1 TO LEN(M$)
         830 CODE=ASC(SEG$(M$,I,1))
         840 CALL HCHAR(Y+I,X,CODE)
         850 NEXT I
         860 RETURN
         870 CALL HCHAR(8+R,4+C,100+B(R,C))
         880 IF KEY=9 THEN 960
         890 C=C-1
         900 IF C<>0 THEN 1020
         910 C=8
         920 R=R-1
         930 IF R<>0 THEN 1020
         940 R=8
         950 GOTO 1020
         960 C=C+1
         970 IF C<>9 THEN 1020
         980 C=1
         990 R=R+1
         1000 IF R<>9 THEN 1020
         1010 R=1
         1020 RETURN
         """.trimIndent(), machine
      )
      assertEquals((100..1020 step 10).toList(), machine.program!!.listing.map { it.lineNumber }, "Accepted program lines")

      var numCallsLine370 = 0
      machine.setKeyboardInputProvider(object : KeyboardInputProvider {
         override fun currentlyPressedKeyCode(ctx: KeyboardInputProvider.CallKeyContext): TiCode? {
            val lineNum = ctx.programLineNumber
            println(lineNum)
            return when (lineNum) {
               370 -> {
                  val result = if (numCallsLine370 % 8 < 4) TiPlainCode.Digit1 else TiPlainCode.Digit0
                  numCallsLine370++
                  result
               }
               730 -> ti994aKeyForCode(81)
               else -> error("Cannot provide key input for program line $lineNum")
            }
         }
      })
      interpreter.interpret("RUN", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            5 to "    12345678       f",
            6 to "   1eeeedddd",
            7 to "   2eeeedddd",
            8 to "   3eeeedddd",
            9 to "   4eeeedddd       fff",
            10 to "   5eeeedddd       fff",
            11 to "   6eeeedddd       fff",
            12 to "   7eeeedddd",
            13 to "   8eeeeddddF0F0F0F0F0F0F0F0",
            15 to "            PRESS Q TO QUIT",
            16 to "            PRESS ANY OTHER",
            17 to "            KEY TO CONTINUE",
            19 to "    0=OFF=WHITE",
            20 to "    1=ON=BLACK",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testGraphicsMatch() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 REM GRAPHICS MATCH
         110 CALL COLOR(9,7,16)
         120 CALL COLOR(10,13,16)
         130 CALL COLOR(11,2,16)
         140 CALL COLOR(12,6,16)
         150 CALL COLOR(13,11,16)
         160 CALL COLOR(14,5,16)
         170 CALL CHAR(96,"00001C3E7F7F7F7F")
         180 CALL CHAR(97,"0000387CFEFEFEFE")
         190 CALL CHAR(98,"3F1F0F070301")
         200 CALL CHAR(99,"FCF8F0E0C080")
         210 CALL CHAR(100,"00000000001F3F7F")
         220 CALL CHAR(104,"0000060810204080")
         230 CALL CHAR(101,"7F7F7F7F3F3F1F")
         240 CALL CHAR(102,"E0F0F0F0F0E0C0")
         250 CALL CHAR(112,"0000010101010101")
         260 CALL CHAR(113,"0000808080808080")
         270 CALL CHAR(120,"03070707070F0701")
         280 CALL CHAR(121,"C0E0E0E0E0F0E080")
         290 CALL CHAR(128,"000000030F1F3FFF")
         300 CALL CHAR(129,"000000C0D0D8FCFF")
         310 CALL CHAR(130,"FF3F1F0F03")
         320 CALL CHAR(131,"FFFCF8F0C0")
         330 CALL CHAR(105,"000103070F1F3F7F")
         340 CALL CHAR(106,"0080C0E0F0F8FCFE")
         350 CALL CHAR(107,"7F3F1F0F070301")
         360 CALL CHAR(108,"FEFCF8F0E0C080")
         370 CALL CHAR(136,"00000000003F3F3F")
         380 CALL CHAR(137,"0000000000FCFCFC")
         390 CALL CHAR(138,"3F3F3F")
         400 CALL CHAR(139,"FCFCFC")
         410 RANDOMIZE
         420 CALL CLEAR
         430 C=14
         440 FOR I=1 TO 3
         450 PIC(I)=INT(6*RND)+1
         460 ON PIC(I) GOSUB 840,900,960,1020,1080,1140
         470 C=C+2
         480 NEXT I
         490 REM SCORING
         500 IF PIC(1)<>PIC(2) THEN 520
         510 IF PIC(2)=PIC(3) THEN 700 ELSE 540
         520 IF PIC(1)<>PIC(3) THEN 610
         530 GOTO 650
         540 IF PIC(1)/2<>INT(PIC(1)/2) THEN 650
         550 TOTAL=TOTAL+40
         560 CALL SOUND(100,440,2)
         570 CALL SOUND(100,660,2)
         580 CALL SOUND(100,550,2)
         590 PRINT "BOUNS--40 POINTS"
         600 GOTO 770
         610 TOTAL=TOTAL-10
         620 CALL SOUND(100,110,1)
         630 PRINT "LOSE 10 POINTS"
         640 GOTO 770
         650 TOTAL=TOTAL+10
         660 CALL SOUND(100,660,2)
         670 CALL SOUND(100,770,2)
         680 PRINT "WIN 10 POINTS"
         690 GOTO 770
         700 TOTAL=TOTAL+75
         710 CALL SOUND(100,440,2)
         720 CALL SOUND(100,550,2)
         730 CALL SOUND(100,440,2)
         740 CALL SOUND(100,660,2)
         750 CALL SOUND(100,880,2)
         760 PRINT "JACKPOT!--75 POINTS"
         770 PRINT "CURRENT TOTAL POINTS: ";TOTAL
         780 PRINT "WANT TO PLAY AGAIN?"
         790 PRINT "PRESS Y FOR YES"
         800 CALL KEY(0,KEY,STATUS)
         810 IF STATUS=0 THEN 800
         820 IF KEY=89 THEN 410
         830 END
         840 REM PRINT HEART
         850 CALL HCHAR(12,C,96)
         860 CALL HCHAR(12,C+1,97)
         870 CALL HCHAR(13,C,98)
         880 CALL HCHAR(13,C+1,99)
         890 RETURN
         900 REM PRINT CHERRY
         910 CALL HCHAR(12,C,100)
         920 CALL HCHAR(12,C+1,104)
         930 CALL HCHAR(13,C,101)
         940 CALL HCHAR(13,C+1,102)
         950 RETURN
         960 REM PRINT BELL
         970 CALL HCHAR(12,C,112)
         980 CALL HCHAR(12,C+1,113)
         990 CALL HCHAR(13,C,120)
         1000 CALL HCHAR(13,C+1,121)
         1010 RETURN
         1020 REM PRINT LEMON
         1030 CALL HCHAR(12,C,128)
         1040 CALL HCHAR(12,C+1,129)
         1050 CALL HCHAR(13,C,130)
         1060 CALL HCHAR(13,C+1,131)
         1070 RETURN
         1080 REM PRINT DIAMOND
         1090 CALL HCHAR(12,C,105)
         1100 CALL HCHAR(12,C+1,106)
         1110 CALL HCHAR(13,C,107)
         1120 CALL HCHAR(13,C+1,108)
         1130 RETURN
         1140 REM PRINT BAR
         1150 CALL HCHAR(12,C,136)
         1160 CALL HCHAR(12,C+1,137)
         1170 CALL HCHAR(13,C,138)
         1180 CALL HCHAR(13,C+1,139)
         1190 RETURN
         """.trimIndent(), machine
      )
      assertEquals((100..1190 step 10).toList(), machine.program!!.listing.map { it.lineNumber }, "Accepted program lines")

      machine.setKeyboardInputProvider(object : KeyboardInputProvider {
         override fun currentlyPressedKeyCode(ctx: KeyboardInputProvider.CallKeyContext): TiCode? {
            val lineNum = ctx.programLineNumber
            println(lineNum)
            return when (lineNum) {
               800 -> TiPlainCode.n
               else -> error("Cannot provide key input for program line $lineNum")
            }
         }
      })
      interpreter.interpret("RUN", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            5 to "             " + 128.toChar() + 129.toChar() + 112.toChar() + 113.toChar() + 128.toChar() + 129.toChar(),
            6 to "             " + 130.toChar() + 131.toChar() + 120.toChar() + 121.toChar() + 130.toChar() + 131.toChar(),
            17 to "  WIN 10 POINTS",
            18 to "  CURRENT TOTAL POINTS:  10",
            19 to "  WANT TO PLAY AGAIN?",
            20 to "  PRESS Y FOR YES",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }
}