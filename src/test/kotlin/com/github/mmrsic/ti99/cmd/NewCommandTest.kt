package com.github.mmrsic.ti99.cmd

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.mmrsic.ti99.basic.NewCommand
import com.github.mmrsic.ti99.basic.betterparse.TiBasicParser
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NewCommandTest {

    @Test
    fun testOnFreshMachineTi994a() {
        val machine = TiBasicModule()
        NewCommand().execute(machine)
        assertEquals(null, machine.program)
        assertEquals(mapOf(), machine.getAllNumericVariableValues())
    }

    @Test
    fun testAfterPrint() {
        val machine = TiBasicModule()
        machine.screen.print("HELLO!")
        NewCommand().execute(machine)
        assertEquals(null, machine.program)
        assertEquals(mapOf(), machine.getAllNumericVariableValues())
    }

    @Test
    fun testParseNewCommand() {
        val parsedCommand = TiBasicParser(TiBasicModule()).parseToEnd("NEW")
        assertTrue(parsedCommand is NewCommand)
    }

    @Test
    fun testParseNewCommandIncludingLeadingAndTrailingBlanks() {
        val parsedCommand = TiBasicParser(TiBasicModule()).parseToEnd(" NEW  ")
        assertTrue(parsedCommand is NewCommand)
    }

    @Ignore("Not yet implemented")
    @Test
    fun testParseNewCommandWithTrailingOneSeparatedByBlank() {
        val parsedCommand = TiBasicParser(TiBasicModule()).parseToEnd("NEW 1")
        assertTrue(parsedCommand is NewCommand)
    }

}