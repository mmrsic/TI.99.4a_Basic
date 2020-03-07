package com.github.mmrsic.ti99

import com.github.mmrsic.ti99.hw.Screen
import kotlin.test.assertEquals

class TestHelperScreen {

    companion object {

        fun assertCursorAt(row: Int, column: Int, screen: Screen) {
            assertEquals(row, screen.cursor?.row, "Cursor row")
            assertEquals(column, screen.cursor?.column, "Cursor column")
        }

        fun assertPrintContents(expRowStrings: Map<Int, String>, screen: Screen) {
            val expText = toWrappedText(expRowStrings)
            val actualText = toWrappedText(screen.strings.nonEmptyRightTrimmed())
            assertEquals(expText, actualText, "Print contents")
        }

        fun assertAllPatternsEqual(expectedPattern: String, screen: Screen) {
            screen.patterns.patternsDo { row, col, actualPattern ->
                assertEquals(expectedPattern, actualPattern, "Pattern at row $row, column $col")
            }
        }

        // HELPERS //

        private fun toWrappedText(lines: Map<Int, String>) = buildString {
            for (line in lines) append(line).append("\n")
        }

    }

}