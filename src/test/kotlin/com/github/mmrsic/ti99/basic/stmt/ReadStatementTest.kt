package com.github.mmrsic.ti99.basic.stmt

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

class ReadStatementTest {

   @Test
   fun testCharDefinitionPattern() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 READ C,C$
            110 DATA 42,0123456789ABCDEF
            120 PRINT "C=";C;", C$=";C$
            130 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            12 to "  TI BASIC READY",
            14 to " >100 READ C,C$",
            15 to " >110 DATA 42,0123456789ABCDEF",
            17 to " >120 PRINT \"C=\";C;\", C$=\";C$",
            18 to " >130 END",
            19 to " >RUN",
            20 to "  C= 42 , C$=0123456789ABCDEF",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }


}