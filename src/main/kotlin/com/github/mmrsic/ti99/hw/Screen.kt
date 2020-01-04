package com.github.mmrsic.ti99.hw

import com.github.mmrsic.ti99.basic.expr.toAsciiCode
import com.github.mmrsic.ti99.basic.expr.toChar

abstract class Screen {
    val codes: CodeScreen = CodeScreen()
    val strings: StringScreen = StringScreen(codes)
    var cursor: Cursor? = null
        internal set

    init {
        clear()
    }

    /**
     * Add ascii codes of a given characters string to the bottom of the screen, scrolling the existing rows one line
     * to the top.
     */
    fun print(characters: String) {
        var leftOver = hchar(24, 3, characters, 30)
        while (leftOver.isNotEmpty()) {
            scroll()
            leftOver = hchar(24, 3, leftOver, 30)
        }
        scroll()
    }

    fun scroll() {
        codes.scroll()
    }

    fun hchar(row: Int, startCol: Int, characters: String, maxCol: Int = TiBasicScreen.MAX_COLUMNS): String {
        if (startCol > maxCol) {
            throw Exception("Start column ($startCol) must not be greater than max column ($maxCol)")
        }
        if (characters.isEmpty()) {
            return ""
        }
        val chunked = characters.chunked(1 + maxCol - startCol)
        val charsToDisplay = chunked[0]
        codes.hchar(row, startCol, charsToDisplay.map { c -> toAsciiCode(c) })
        return chunked.subList(1, chunked.size).joinToString()
    }

    fun clear() = codes.clear()

    fun acceptAt(row: Int, column: Int, prompt: String = "") {
        if (prompt.isNotEmpty()) {
            strings.displayAt(row, column, prompt)
        }
        cursor = Cursor(row, column + prompt.length)
    }

}

class TiBasicScreen : Screen() {

    companion object {
        const val MAX_ROWS = 24
        const val MAX_COLUMNS = 32
        const val PIXEL_WIDTH = MAX_COLUMNS * 8
        const val PIXEL_HEIGHT = MAX_ROWS * 8
    }

}

class Cursor(val row: Int, val column: Int)

class CodeScreen {
    private val codeTable = mutableMapOf<Pair<Int, Int>, Int>()
    /** The code for the default character of the screen. */
    private val defaultChrCode = 32

    /** The ASCII code at a given row and a given column within that row. */
    fun codeAt(row: Int, col: Int): Int {
        return when (val mapValue = codeTable[Pair(row, col)]) {
            null -> defaultChrCode
            else -> mapValue
        }
    }

    private fun patternAt(row: Int, col: Int): String {
        return defaultCharPattern(codeAt(row, col))
    }

    /** Put a given list of ASCII codes into a given row starting at a given column of that row. */
    internal fun putAll(row: Int, column: Int, codes: List<Int>) {
        for ((colOffset, code) in codes.withIndex()) {
            codeTable[Pair(row, column + colOffset)] = code
        }
    }

    fun patternsDo(lambda: (Int, Int, String) -> Unit) {
        for (row in 1..TiBasicScreen.MAX_ROWS) {
            for (col in 1..TiBasicScreen.MAX_COLUMNS) {
                lambda.invoke(row, col, patternAt(row, col))
            }
        }
    }

    /**
     * Place a list of character codes horizontally at a given row at a given start index extending the codes to the
     * right.
     */
    fun hchar(row: Int, startCol: Int, codes: List<Int>) {
        for ((index, code) in codes.withIndex()) {
            codeTable[Pair(row, startCol + index)] = code
        }
    }

    /**
     * Scroll the codes from bottom to top, replacing the top line with a newly created bottom line full
     * [defaultChrCode].
     */
    fun scroll() {
        val old = codeTable.toMap()
        codeTable.clear()
        for (oldEntry in old.entries) {
            val oldKey = oldEntry.key
            if (oldKey.first > 1) {
                val newKey = Pair(oldKey.first - 1, oldKey.second)
                codeTable[newKey] = oldEntry.value
            }
        }
    }

    /** Clear the code table, that is, set the [defaultChrCode] onto each cell of the screen. */
    fun clear() = codeTable.clear()

}

class StringScreen(private val codes: CodeScreen) {

    fun withoutTrailingBlanks(row: Int, column: Int): String {
        val rowCandidateBuilder = StringBuilder()
        for (col in column..TiBasicScreen.MAX_COLUMNS) {
            rowCandidateBuilder.append(toChar(codes.codeAt(row, col)))
        }
        return rowCandidateBuilder.trimEnd().toString()
    }

    /**
     * The non-empty string lines of the screen where all leading and trailing spaces are trimmed.
     */
    fun nonEmptyRightTrimmed(): Map<Int, String> {
        val result = mutableMapOf<Int, String>()
        for (row in 1..TiBasicScreen.MAX_ROWS) {
            val rowCandidateBuilder = StringBuilder()
            for (col in 1..TiBasicScreen.MAX_COLUMNS) {
                rowCandidateBuilder.append(toChar(codes.codeAt(row, col)))
            }
            val rowValue = rowCandidateBuilder.trimEnd()
            if (rowValue.isNotEmpty()) {
                result[row] = rowValue.toString()
            }
        }
        return result.toMap()
    }

    internal fun displayAt(row: Int, column: Int, text: String) {
        codes.putAll(row, column, text.map { toAsciiCode(it) })
    }

}


fun defaultCharPattern(code: Int): String {
    return when (code) {
        toAsciiCode('A') -> "003844447C444444"
        toAsciiCode('B') -> "0078242438242478"
        toAsciiCode('C') -> "0038444040404438"
        toAsciiCode('D') -> "0078242424242478"
        toAsciiCode('E') -> "007C40407840407C"
        toAsciiCode('I') -> "0038101010101038"
        toAsciiCode('R') -> "0078444478504844"
        toAsciiCode('S') -> "0038444038044438"
        toAsciiCode('T') -> "007C101010101010"
        toAsciiCode('Y') -> "0044442810101010"
        toAsciiCode('>') -> "0020100804081020"
        else -> "0000000000000000"
    }
}