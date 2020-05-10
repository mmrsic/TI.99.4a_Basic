package com.github.mmrsic.ti99

import com.github.mmrsic.ti99.hw.Screen
import com.github.mmrsic.ti99.hw.TiCharacterColor
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
         screen.patterns.forEachCellDo { row, col, actualPattern ->
            assertEquals(expectedPattern, actualPattern.hex, "Pattern at row $row, column $col")
         }
      }

      fun assertPatterns(validateCell: (Int, Int, String) -> Boolean, screen: Screen) {
         screen.patterns.forEachCellDo { row, col, actualPattern ->
            assert(validateCell(row, col, actualPattern.hex)) { "Wrong pattern at row $row, column $col: $actualPattern" }
         }
      }

      fun assertAllColorsEqual(expectedColor: TiCharacterColor, screen: Screen) {
         assertColors({ _, _, charColors -> charColors == expectedColor }, screen)
      }

      fun assertColors(validateCell: (Int, Int, TiCharacterColor) -> Boolean, screen: Screen) {
         screen.colors.forEachCellDo { row, col, charColors ->
            assert(validateCell(row, col, charColors)) { "Wrong colors at row $row, column $col: $charColors" }
         }
      }

      // HELPERS //

      private fun toWrappedText(lines: Map<Int, String>) = buildString {
         for (line in lines) append(line).append("\n")
      }

   }

}