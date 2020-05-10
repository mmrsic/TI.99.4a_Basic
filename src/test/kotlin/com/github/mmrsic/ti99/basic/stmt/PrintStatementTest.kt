package com.github.mmrsic.ti99.basic.stmt

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.mmrsic.ti99.basic.PrintStatement
import com.github.mmrsic.ti99.basic.betterparse.TiBasicParser
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test
import kotlin.test.assertTrue

class PrintStatementTest {

   @Test
   fun testPlain() {
      val machine = TiBasicModule()
      val result = TiBasicParser(machine).parseToEnd("PRINT")
      assertTrue(result is PrintStatement)
      result.execute(machine)
   }

   @Test
   fun testPrintA() {
      val machine = TiBasicModule()
      val result = TiBasicParser(machine).parseToEnd("PRINT A")
      assertTrue(result is PrintStatement)
      result.execute(machine)
   }

   @Test
   fun testPrintAPlusB() {
      val machine = TiBasicModule()
      val result = TiBasicParser(machine).parseToEnd("PRINT A+B")
      assertTrue(result is PrintStatement)
      result.execute(machine)
   }

}