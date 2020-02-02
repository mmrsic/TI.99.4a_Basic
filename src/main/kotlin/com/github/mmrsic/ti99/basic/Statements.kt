package com.github.mmrsic.ti99.basic

import com.github.mmrsic.ti99.basic.expr.Expression
import com.github.mmrsic.ti99.basic.expr.NumericExpr
import com.github.mmrsic.ti99.basic.expr.StringExpr
import com.github.mmrsic.ti99.hw.TiBasicModule

/**
 * A [TiBasicExecutable] that may be used within a [TiBasicProgram].
 */
interface Statement : TiBasicExecutable {
    /** Text used to print this statement on the screen when the LIST command is executed. */
    fun listText(): String
}

/** Marker for instances which are skipped on [ContinueCommand]. */
interface SkippedOnContinue

/**
 * A [Statement] that may depend on at least one line number of a program
 */
interface LineNumberDependentStatement : Statement {
    fun changeLineNumbers(lineNumbersMapping: Map<Int, Int>)
}

class BreakStatement(private val lineNumberList: List<Int>? = null) : Statement, SkippedOnContinue {
    override fun listText() = if (lineNumberList != null) "BREAK $lineNumberList" else "BREAK"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        programLineNumber ?: throw IllegalArgumentException("Break statement may not be used without program")
        if (lineNumberList == null) throw Breakpoint()
        if (lineNumberList.isEmpty()) throw Breakpoint()
        machine.addBreakpoints(lineNumberList, programLineNumber)
    }
}

class PrintStatement(private val expressions: List<Expression>) : Statement, Command {
    override val name: String = "PRINT"
    override fun listText(): String {
        if (expressions.isEmpty()) return name
        return "$name " + expressions.joinToString("") { it.listText() }
    }

    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        machine.printTokens(expressions, programLineNumber)
    }
}

class LetNumberStatement(val varName: String, val expr: NumericExpr) : Statement {
    override fun listText(): String = "$varName=${expr.listText().trim()}" // TODO: Add optional LET
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        machine.setNumericVariable(varName, expr)
    }
}

class LetStringStatement(val varName: String, val expr: StringExpr) : Statement {
    override fun listText(): String = "$varName=${expr.listText()}" // TODO: Add optional LET
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        machine.setStringVariable(varName, expr)
    }
}

class EndStatement : Statement {
    override fun listText() = "END"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        machine.endProgramRun()
    }
}

class RemarkStatement(val text: String) : Statement {
    override fun listText(): String = "REM $text"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) = println("Remark: $text")
}

class GoToStatement(originalLineNum: Int) : LineNumberDependentStatement {
    private var lineNumber: Int = originalLineNum

    override fun listText(): String = "GO TO $lineNumber"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        TODO("not implemented")
    }

    override fun changeLineNumbers(lineNumbersMapping: Map<Int, Int>) {
        lineNumber = lineNumbersMapping[lineNumber]!!
    }
}

class UnbreakStatement(private val lineNumberList: List<Int>? = null) : Statement {
    override fun listText() = if (lineNumberList != null) "UNBREAK $lineNumberList" else "UNBREAK"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        programLineNumber ?: throw IllegalArgumentException("Unbreak statement may not be used without program")
        if (lineNumberList == null) {
            machine.removeBreakpoints(programLineNumber = programLineNumber)
        } else {
            machine.removeBreakpoints(lineNumberList, programLineNumber)
        }
    }
}

class ForToStepStatement(val initializer: LetNumberStatement, val limit: NumericExpr) : Statement {
    override fun listText(): String {
        return "FOR ${initializer.listText().trim()} TO ${limit.listText()}"
    }

    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        val pln = programLineNumber ?: throw IllegalArgumentException("$this can be used as a statement only")
        val interpreter = machine.programInterpreter ?: throw IllegalArgumentException(
            "Machine program interpreter must be present for $this"
        )
        interpreter.beginForLoop(pln, initializer, limit)
    }

    override fun requiresEmptyLineAfterExecution() = false
}

class NextStatement(val varName: String) : Statement {
    override fun listText() = "NEXT $varName"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        val interpreter = machine.programInterpreter ?: throw IllegalArgumentException(
            "Machine program interpreter must be present for $this"
        )
        interpreter.nextForLoopStep(varName)
    }

    override fun requiresEmptyLineAfterExecution() = false
}