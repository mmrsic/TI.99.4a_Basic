package com.github.mmrsic.ti99.basic

import com.github.mmrsic.ti99.basic.expr.NumericConstant
import com.github.mmrsic.ti99.basic.expr.NumericExpr
import com.github.mmrsic.ti99.basic.expr.StringConstant
import com.github.mmrsic.ti99.hw.TiBasicModule

/**
 * The CHAR subprogram allows you to define special graphics characters. You can redefine the standard sets of
 * characters (ASCII codes 32-127) and the undefined characters, ASCII codes 128-159.
 * The CHAR subprogram is the inverse of the [CharpatSubprogram].
 */
class CharSubprogram(private val code: NumericExpr, private val pattern: StringConstant) : Statement, Command {
    override val name = "CHAR"
    override fun listText() = "CALL $name(${code.listText().trim()},${pattern.listText()})"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        val characterCode = code.value().toNative().toInt()
        val patternIdentifier = pattern.constant
        machine.defineCharacter(characterCode, patternIdentifier)
    }
}

/**
 * The CLEAR subprogram is used to clear (erase) the entire screen. When the CLEAR subprogram is called, the space
 * character (ASCII code 32) is placed in all positions on the screen.
 */
class ClearSubprogram : Statement, Command {
    override val name = "CLEAR"
    override fun listText() = "CALL $name"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) = machine.screen.clear()
    override fun requiresEmptyLineAfterExecution() = false
}

/**
 * The COLOR subprogram provides a powerful design capability by allowing you to specify screen character colors. (To
 * change the screen color itself, see the [ScreenSubprogram].)
 *
 *  Each character displayed on your computer screen has two colors. The color of the dots that make up the character
 * itself is called the foreground color. The color that occupies the rest of the character position on the screen is
 * called the background color. Sixteen colors are available on the TI computer, so your entries for foreground and
 * background color must have a value of 1 through 16. The color codes are given in the table below.
 *
 * If transparent (code 1) is specified, the present screen color shows through when a character is displayed. Until
 * a CALL COLOR is performed, the standard foreground is black (code 2) and the standard background color is transparent
 * (code 1) for all characters. When a breakpoint occurs, all characters are reset to the standard colors.
 *
 * ```
 * | Color Code |      Color     |
 * |      1     |  Transparent   |
 * |      2     |  Black         |
 * |      3     |  Medium Green  |
 * |      4     |  Light Green   |
 * |      5     |  Dark Blue     |
 * |      6     |  Light Blue    |
 * |      7     |  Dark Red      |
 * |      8     |  Cyan          |
 * |      9     |  Medium Red    |
 * |     10     |  Light Red     |
 * |     11     |  Dark Yellow   |
 * |     12     |  Light Yellow  |
 * |     13     |  Dark Green    |
 * |     14     |  Magenta       |
 * |     15     |  Gray          |
 * |     16     |  White         |
 * ```
 */
class ColorSubprogram(
    val characterSetNumber: NumericExpr,
    val foregrColorCode: NumericExpr,
    val backgrColorCode: NumericExpr
) : Statement, Command {
    override val name = "COLOR"
    override fun listText() =
        "CALL $name(${characterSetNumber.listText()},${foregrColorCode.listText()},${backgrColorCode.listText()})"

    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        machine.setColor(characterSetNumber.value(), foregrColorCode.value(), backgrColorCode.value())
    }
}

class HcharSubprogram(
    private val row: NumericExpr, private val column: NumericExpr,
    private val characterCode: NumericExpr, private val repetition: NumericExpr = NumericConstant(1)
) : Statement, Command {
    override val name = "HCHAR"
    override fun listText(): String {
        val rowPart = row.listText().trim()
        val columnPart = column.listText().trim()
        val codePart = characterCode.listText().trim()
        val optionalRepetitionPart = if (repetition != null) ",${repetition.listText().trim()}" else ""
        return "CALL $name($rowPart,$columnPart,$codePart$optionalRepetitionPart)"
    }

    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        val row = row.value().toNative().toInt()
        val column = column.value().toNative().toInt()
        val characterCode = characterCode.value().toNative().toInt()
        val repetition = repetition.value().toNative().toInt()
        machine.screen.hchar(row, column, characterCode, repetition)
    }
}