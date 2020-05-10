package com.github.mmrsic.ti99.cmd

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.mmrsic.ti99.basic.RunCommand
import com.github.mmrsic.ti99.basic.betterparse.TiBasicParser
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test
import kotlin.test.assertTrue

class RunCommandTest {

   @Test
   fun testParseRun() {
      val parsedCommand = TiBasicParser(TiBasicModule()).parseToEnd("RUN")
      assertTrue(parsedCommand is RunCommand)
   }

   @Test
   fun testParseRunWithLeadingAndTrailingBlanks() {
      val parsedCommand = TiBasicParser(TiBasicModule()).parseToEnd(" RUN   ")
      assertTrue(parsedCommand is RunCommand)
   }

   @Test
   fun testParseRunWithLineNumber() {
      val parsedCommand = TiBasicParser(TiBasicModule()).parseToEnd(" RUN 120")
      assertTrue(parsedCommand is RunCommand)
   }

}