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