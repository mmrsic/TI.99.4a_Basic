package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.basic.expr.NumericConstant
import com.github.mmrsic.ti99.hw.KeyboardInputProvider
import com.github.mmrsic.ti99.hw.TiBasicModule
import com.github.mmrsic.ti99.hw.TiCode
import com.github.mmrsic.ti99.hw.TiShiftCode
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Test cases for examples found in User's Reference Guide on pages II-87 through II-89.
 */
class CallKeyTest {

   @Test
   fun testKeyCodeToSoundNote() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
                100 CALL KEY(0,KEY,STATUS)
                110 IF STATUS=0 THEN 100
                120 NOTE=KEY-64
                130 ON NOTE GOTO 250,270,150,170,190,210,230
                140 GOTO 100
                150 NOTE=262
                160 GOTO 280
                170 NOTE=294
                180 GOTO 280
                190 NOTE=330
                200 GOTO 280
                210 NOTE=349
                220 GOTO 280
                230 NOTE=392
                240 GOTO 280
                250 NOTE=440
                260 GOTO 280
                270 NOTE=494
                280 CALL SOUND(100,NOTE,2)
                290 GOTO 100
            """.trimIndent(), machine
      )
      var numProviderCalls = 0
      machine.setKeyboardInputProvider(object : KeyboardInputProvider {
         override fun currentlyPressedKeyCode(ctx: KeyboardInputProvider.CallKeyContext): TiCode? {
            numProviderCalls++
            return when (numProviderCalls) {
               1 -> TiShiftCode.A
               2 -> TiShiftCode.B
               3 -> TiShiftCode.C
               4 -> TiShiftCode.D
               5 -> TiShiftCode.E
               6 -> TiShiftCode.F
               7 -> TiShiftCode.G
               8 -> TiShiftCode.H
               else -> throw NotImplementedError("Cannot provide key for call #$numProviderCalls")
            }
         }
      })
      var numHookCalls = 0
      machine.addProgramLineHookAfterLine(120) {
         numHookCalls++
         if (numHookCalls == 10) machine.addBreakpoint(130) // Just in case bad value would not be recognized
         assertEquals(
            NumericConstant(64 + numHookCalls),
            machine.getNumericVariableValue("KEY"),
            "Value of variable KEY"
         )
      }
      interpreter.interpret("RUN", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            1 to " >120 NOTE=KEY-64",
            2 to " >130 ON NOTE GOTO 250,270,150",
            3 to "  ,170,190,210,230",
            4 to " >140 GOTO 100",
            5 to " >150 NOTE=262",
            6 to " >160 GOTO 280",
            7 to " >170 NOTE=294",
            8 to " >180 GOTO 280",
            9 to " >190 NOTE=330",
            10 to " >200 GOTO 280",
            11 to " >210 NOTE=349",
            12 to " >220 GOTO 280",
            13 to " >230 NOTE=392",
            14 to " >240 GOTO 280",
            15 to " >250 NOTE=440",
            16 to " >260 GOTO 280",
            17 to " >270 NOTE=494",
            18 to " >280 CALL SOUND(100,NOTE,2)",
            19 to " >290 GOTO 100",
            20 to " >RUN",
            22 to "  * BAD VALUE IN 130",
            24 to " >"
         ), machine.screen
      )
   }

}