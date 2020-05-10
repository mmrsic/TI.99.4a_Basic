package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on page II-49.
 */
class GotoStatementTest {

   @Test
   fun testGotoAsElseBranchOfIfStatement() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
                100 REM HOW MANY GIFTS ON   THE 12 DAYS OF CHRISTMAS?
                110 GIFTS=0
                120 DAYS=1
                130 COUNT=0
                140 COUNT=COUNT+1
                150 GIFTS=GIFTS+1
                160 IF COUNT=DAYS THEN 180
                170 GOTO 140
                180 DAYS=DAYS+1
                190 IF DAYS<=12 THEN 130
                200 PRINT "TOTAL NUMBER OF GIFTS IS";GIFTS
                210 END
                RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            3 to "  TI BASIC READY",
            5 to " >100 REM HOW MANY GIFTS ON",
            6 to "  THE 12 DAYS OF CHRISTMAS?",
            7 to " >110 GIFTS=0",
            8 to " >120 DAYS=1",
            9 to " >130 COUNT=0",
            10 to " >140 COUNT=COUNT+1",
            11 to " >150 GIFTS=GIFTS+1",
            12 to " >160 IF COUNT=DAYS THEN 180",
            13 to " >170 GOTO 140",
            14 to " >180 DAYS=DAYS+1",
            15 to " >190 IF DAYS<=12 THEN 130",
            16 to " >200 PRINT \"TOTAL NUMBER OF G",
            17 to "  IFTS IS\";GIFTS",
            18 to " >210 END",
            19 to " >RUN",
            20 to "  TOTAL NUMBER OF GIFTS IS 78",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

}