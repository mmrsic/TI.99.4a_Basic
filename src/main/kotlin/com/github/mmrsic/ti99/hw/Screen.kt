package com.github.mmrsic.ti99.hw

import com.github.mmrsic.ti99.basic.expr.toAsciiCode
import com.github.mmrsic.ti99.basic.expr.toChar
import java.awt.Image
import java.awt.image.BufferedImage

/**
 * Representation of the TI 99/4a's screen (graphics chip) where codes, strings, and patterns may be printed.
 */
abstract class Screen(getCharPattern: (Int) -> CharacterPattern, colorSets: TiBasicColors) {

   /** Screen representation with ASCII codes for all cells of 24 rows * 32 columns. */
   val codes: CodeScreen = CodeScreen()

   /** Screen representation where the [codes] are interpreted as rows of [String]s. */
   val strings: StringScreen = StringScreen(codes)

   /** Screen representation where the [codes] are interpreted as rows of character patterns. */
   val patterns: PatternScreen = PatternScreen(codes, getCharPattern)

   /** Screen representation where the [codes] are interpreted as rows of foreground/background pairs. */
   val colors: ColorScreen = ColorScreen(codes, colorSets)

   /** The optional cursor currently placed on this screen. */
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
      display(characters)
      scroll()
   }

   /** Add ASCII codes of a given characters string to the bottom, scrolling the contents only if it doesn't fit. */
   fun display(characters: String, startRow: Int = 24, leftMarginCol: Int = 3, rightMarginCol: Int = 30) {
      var leftOver = hchar(startRow, leftMarginCol, characters, rightMarginCol)
      while (leftOver.isNotEmpty()) {
         scroll()
         leftOver = hchar(startRow, leftMarginCol, leftOver, rightMarginCol)
      }
   }

   /** Scroll the screen contents up one row. */
   fun scroll() {
      codes.scroll()
   }

   /** Place a given characters string onto this screen. */
   fun hchar(row: Int, startCol: Int, characters: String, maxCol: Int = TiBasicScreen.NUM_COLUMNS): String {
      if (startCol > maxCol) error("Start column ($startCol) must not be greater than max column ($maxCol)")
      if (characters.isEmpty()) return ""
      val chunked = characters.chunked(1 + maxCol - startCol)
      val charsToDisplay = chunked[0]
      codes.hchar(row, startCol, charsToDisplay.map { c -> toAsciiCode(c) })
      return chunked.subList(1, chunked.size).joinToString("")
   }

   /** Place a given code (repeatedly) onto this screen. */
   fun hchar(row: Int, col: Int, code: Int, repetition: Int) {
      codes.hchar(row, col, List(repetition) { code })
   }

   /** Place a given code (repeatedly) onto this screen. */
   fun vchar(row: Int, col: Int, code: Int, repetition: Int) {
      codes.vchar(row, col, List(repetition) { code })
   }

   /** Clear the whole screen, that is, set code 32 (space) at each and every position. */
   fun clear() = codes.clear()

   /** Place the input accepting cursor with an optional prompt text onto this screen. */
   fun acceptAt(row: Int, column: Int, prompt: String = "") {
      if (prompt.isNotEmpty()) {
         strings.displayAt(row, column, prompt)
      }
      cursor = Cursor(row, column + prompt.length)
   }

   /** An image representing the current snapshot of the screen. */
   abstract fun getSnapshot(): Image

}

/**
 * TI Basic's representation of the [Screen].
 */
class TiBasicScreen(getCharPattern: (Int) -> CharacterPattern, colors: TiBasicColors) : Screen(getCharPattern, colors) {

   companion object {
      const val NUM_ROWS = 24
      const val NUM_COLUMNS = 32
      const val FIRST_PRINT_COLUMN = 3
      const val LAST_PRINT_COLUMN = 30
      const val NUM_PRINT_COLUMNS = LAST_PRINT_COLUMN - FIRST_PRINT_COLUMN + 1
      const val CHAR_PIXELS_X = 8
      const val CHAR_PIXELS_Y = 8
      const val PIXEL_WIDTH = NUM_COLUMNS * CHAR_PIXELS_X
      const val PIXEL_HEIGHT = NUM_ROWS * CHAR_PIXELS_Y
      val DEFAULT_CHAR_COLORS = TiCharacterColor(TiColor.Black, TiColor.Transparent)
      val DEFAULT_BACKGROUND_COLOR = TiColor.Cyan
   }

   /** An image representing the current snapshot of the screen. */
   override fun getSnapshot(): Image {
      val result = BufferedImage(PIXEL_WIDTH, PIXEL_HEIGHT, BufferedImage.TYPE_INT_ARGB)
      patterns.forEachCellDo { row, col, pattern ->
         val patternY = (row - 1) * CHAR_PIXELS_Y
         val patternX = (col - 1) * CHAR_PIXELS_X
         val patternColors = colors.characterColorAt(row, col)
         val fgColor = patternColors.foreground
         val bgColor = patternColors.background
         pattern.binary.withIndex().forEach { indexedBit ->
            val x = patternX + indexedBit.index % CHAR_PIXELS_X
            val y = patternY + indexedBit.index / CHAR_PIXELS_X
            val tiColor = if (indexedBit.value != '0') fgColor else bgColor
            try {
               result.setRGB(x, y, tiColor.rgb)
            } catch (e: Exception) {
               error("Failed to set pixel @$x/$y to ${tiColor.rgb}")
            }
         }
      }
      return result
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

   /** Put a given list of ASCII codes into a given row starting at a given column of that row. */
   internal fun putAll(row: Int, column: Int, codes: List<Int>) {
      for ((colOffset, code) in codes.withIndex()) {
         setCode(row, column + colOffset, code)
      }
   }

   /**
    * Place a list of character codes horizontally at a given row at a given start index extending the codes to the
    * right.
    */
   fun hchar(startRow: Int, startCol: Int, codes: List<Int>) {
      val numColumns = TiBasicScreen.NUM_COLUMNS
      for ((index, code) in codes.withIndex()) {
         val row = startRow + index / numColumns
         val col = startCol + index % numColumns
         setCode(row, col, code % 256)
      }
   }

   /**
    * Place a list of character codes vertically at a given row at a given start index extending the codes to the
    * bottom.
    */
   fun vchar(startRow: Int, startCol: Int, codes: List<Int>) {
      val numRows = TiBasicScreen.NUM_ROWS
      for ((index, code) in codes.withIndex()) {
         val col = startCol + index / numRows
         val row = startRow + index % numRows
         setCode(row, col, code % 256)
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
         if (oldKey.first > 1) setCode(oldKey.first - 1, oldKey.second, oldEntry.value)
      }
   }

   /** Clear the code table, that is, set the [defaultChrCode] onto each cell of the screen. */
   fun clear() = codeTable.clear()

   // HELPERS //

   private fun setCode(row: Int, column: Int, value: Int) {
      if (row !in 1..TiBasicScreen.NUM_ROWS) throw IllegalArgumentException("Illegal row: $row")
      if (column !in 1..TiBasicScreen.NUM_COLUMNS) throw IllegalArgumentException("Illegal column: $column")
      codeTable[Pair(row, column)] = value
   }

}

/**
 * A TI Basic screen representation where the currently presented codes are interpreted as string characters.
 */
class StringScreen(private val codes: CodeScreen) {

   /**
    * A (part) of a screen row given by row and column indices (starting at 1), where trailing blanks are always left out of the
    * result.
    */
   fun withoutTrailingBlanks(row: Int, column: Int) = buildString {
      for (col in column..TiBasicScreen.NUM_COLUMNS) {
         append(toChar(codes.codeAt(row, col)))
      }
   }.trimEnd()

   /**
    * The non-empty string lines of the screen where all leading and trailing spaces are trimmed.
    */
   fun nonEmptyRightTrimmed(): Map<Int, String> {
      val result = mutableMapOf<Int, String>()
      for (row in 1..TiBasicScreen.NUM_ROWS) {
         val rowCandidateBuilder = StringBuilder()
         for (col in 1..TiBasicScreen.NUM_COLUMNS) {
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

class PatternScreen(private val codes: CodeScreen, private val defaultPatterns: (Int) -> CharacterPattern) {

   private val definedPatterns: Map<Int, CharacterPattern> = mutableMapOf()

   /** Row-wise execute a piece of code for all character patterns at each and every cell of this pattern screen. */
   fun forEachCellDo(execute: (Int, Int, CharacterPattern) -> Unit) {
      for (row in 1..TiBasicScreen.NUM_ROWS) {
         for (col in 1..TiBasicScreen.NUM_COLUMNS) {
            execute(row, col, at(row, col))
         }
      }
   }

   /** The current character pattern at a given row/column combination. */
   fun at(row: Int, col: Int): CharacterPattern {
      val code = codes.codeAt(row, col)
      return if (definedPatterns.containsKey(code)) definedPatterns.getValue(code) else defaultPatterns(code)
   }

}

class ColorScreen(private val codes: CodeScreen, private val colors: TiBasicColors) {

   fun forEachCellDo(execute: (Int, Int, TiCharacterColor) -> Unit) {
      for (row in 1..TiBasicScreen.NUM_ROWS) {
         for (col in 1..TiBasicScreen.NUM_COLUMNS) {
            val charColor = characterColorAt(row, col)
            execute(row, col, charColor)
         }
      }
   }

   /**
    * TI character color currently set at a given row/column combo.
    * @param row row value on the screen - must be between 1 and 24
    * @param column column value on the screen - must be between 1 and 32
    */
   fun characterColorAt(row: Int, column: Int): TiCharacterColor {
      val colorSet = colorSet(codes.codeAt(row, column))
      return colors.getColor(TiColorSet.withNumber(colorSet)).replaceTransparentBy(colors.background)
   }

   // HELPERS //

   private fun colorSet(charCode: Int): Int {
      return when (charCode) {
         in 32..39 -> 1
         in 40..47 -> 2
         in 48..55 -> 3
         in 56..63 -> 4
         in 64..71 -> 5
         in 72..79 -> 6
         in 80..87 -> 7
         in 88..95 -> 8
         in 96..103 -> 9
         in 104..111 -> 10
         in 112..119 -> 11
         in 120..127 -> 12
         in 128..135 -> 13
         in 136..143 -> 14
         in 144..151 -> 15
         in 152..159 -> 16
         else -> throw IllegalArgumentException("Illegal character code for color set: $charCode")
      }
   }

}
