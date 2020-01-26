package com.github.mmrsic.ti99.basic

import com.github.mmrsic.ti99.basic.expr.Expression
import com.github.mmrsic.ti99.basic.expr.NumericExpr
import com.github.mmrsic.ti99.basic.expr.StringExpr
import com.github.mmrsic.ti99.hw.TiBasicModule
import com.github.mmrsic.ti99.hw.TiBasicScreen

/**
 * A [TiBasicExecutable] that may be used within a [TiBasicProgram].
 */
interface Statement : TiBasicExecutable {
    /** Text used to print this statement on the screen when the LIST command is executed. */
    fun listText(): String
}

interface SkippedOnContinue

class BreakStatement(private val lineNumberList: List<Int>? = null) : Statement, SkippedOnContinue {
    override fun listText() = if (lineNumberList != null) "BREAK $lineNumberList" else "BREAK"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        programLineNumber ?: throw IllegalArgumentException("Break statement may not be used without program")
        if (lineNumberList == null) throw Breakpoint()
        if (lineNumberList.isNotEmpty()) machine.setBreakpoints(lineNumberList) else throw Breakpoint()
    }
}

/**
* A [Statement] that may depend on at least one line number of a program
 */
interface LineNumberDependentStatement : Statement {
    fun changeLineNumbers(lineNumbersMapping: Map<Int, Int>)
}

class PrintStatement(private val expressions: List<Any>) : Statement, Command {

    override val name: String = "PRINT"

    override fun listText(): String {
        if (expressions.isEmpty()) {
            return name
        }
        return "$name " + expressions.joinToString("") { if (it is Expression) it.listText() else it.toString() }
    }

    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        val maxCol = TiBasicScreen.MAX_COLUMNS - 2
        val currRow = 24
        var currCol = 3
        for (expression in expressions) {
            if (expression is NumericExpr) {
                expression.visitAllValues { nc ->
                    if (nc.isOverflow) {
                        machine.screen.print("")
                        machine.screen.print("* WARNING:")
                        machine.screen.print("  NUMBER TOO BIG" + if (programLineNumber != null) " IN $programLineNumber" else "")
                    }
                }
            }
            if (expression is Expression) {
                val characters = expression.displayValue()
                var lefOver = machine.screen.hchar(currRow, currCol, characters, maxCol)
                currCol += characters.length - lefOver.length
                while (lefOver.isNotEmpty()) {
                    machine.screen.scroll()
                    currCol = 3
                    val last = lefOver
                    lefOver = machine.screen.hchar(currRow, currCol, last, maxCol)
                    currCol += lefOver.length - last.length
                }
            } else {
                println("Ignored in print statement: $expression")
            }
        }
        machine.screen.scroll()
    }
}

class LetNumberStatement(val varName: String, val expr: NumericExpr) : Statement {
    override fun listText(): String = "$varName=${expr.listText().trim()}" // TODO: Add optional LET
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) = machine.setNumericVariable(varName, expr)
}

class LetStringStatement(val varName: String, val expr: StringExpr) : Statement {
    override fun listText(): String = "$varName=${expr.listText()}" // TODO: Add optional LET

    override fun execute(machine: TiBasicModule, programLineNumber: Int?) = machine.setStringVariable(varName, expr)
}

class EndStatement : Statement {
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        // TODO: End current program run
    }

    override fun listText(): String {
        return "END"
    }
}

class RemarkStatement(val text: String) : Statement {

    override fun execute(machine: TiBasicModule, programLineNumber: Int?) = println("Remark: $text")
    override fun listText(): String = "REM $text"

}

class GoToStatement(originalLineNum: Int) : LineNumberDependentStatement {

    private var lineNumber: Int = originalLineNum

    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        TODO("not implemented")
    }

    override fun changeLineNumbers(lineNumbersMapping: Map<Int, Int>) {
        lineNumber = lineNumbersMapping[lineNumber]!!
    }

    override fun listText(): String = "GO TO $lineNumber"

}