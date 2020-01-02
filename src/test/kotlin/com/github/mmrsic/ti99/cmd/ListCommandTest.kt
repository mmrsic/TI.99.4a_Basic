package com.github.mmrsic.ti99.cmd

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.mmrsic.ti99.basic.ListCommand
import com.github.mmrsic.ti99.basic.betterparse.TiBasicParser
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ListCommandTest {

    @Test
    fun testParseList() {
        val parsedCommand = TiBasicParser(TiBasicModule()).parseToEnd("LIST")
        assertTrue(parsedCommand is ListCommand)
        assertEquals(null, parsedCommand.start)
        assertEquals(null, parsedCommand.end)
    }

    @Test
    fun testParseListLine() {
        val parsedCommand = TiBasicParser(TiBasicModule()).parseToEnd("LIST 13")
        assertTrue(parsedCommand is ListCommand)
        assertEquals(13, parsedCommand.start)
        assertEquals(null, parsedCommand.end)
    }

    @Test
    fun testParseListGreaterThan() {
        val parsedCommand = TiBasicParser(TiBasicModule()).parseToEnd("LIST 10-")
        assertTrue(parsedCommand is ListCommand)
        assertEquals(10, parsedCommand.start)
        assertEquals(null, parsedCommand.end)
    }

    @Test
    fun testParseListLessThan() {
        val parsedCommand = TiBasicParser(TiBasicModule()).parseToEnd("LIST -2000")
        assertTrue(parsedCommand is ListCommand)
    }

    @Test
    fun testParseListBetween() {
        val parsedCommand = TiBasicParser(TiBasicModule()).parseToEnd("LIST 110-120")
        assertTrue(parsedCommand is ListCommand)
        assertEquals(110, parsedCommand.start)
        assertEquals(120, parsedCommand.end)
    }

}