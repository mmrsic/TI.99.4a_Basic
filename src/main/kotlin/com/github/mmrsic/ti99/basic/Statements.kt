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

/**
 * The LET statement allows you to assign values to variables in your program. The computer evaluates the expression
 * to the right of the equals sign and puts its value into the variable specified to the left of the equals sign.
 */
abstract class LetStatement(val varName: String) : Statement {
    abstract val expr: Expression
    override fun listText(): String = "$varName=${expr.listText().trim()}" // TODO: Add optional LET
}

/**
 * [LetStatement] where a [NumericExpr] is assigned to a numeric variable.
 * The rules governing underflow and overflow for the evaluation of numeric expressions are used in the LET statement.
 */
class LetNumberStatement(varName: String, override val expr: NumericExpr) : LetStatement(varName) {
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        machine.setNumericVariable(varName, expr)
    }
}

/**
 * [LetStatement] where a [StringExpr] is assigned to a string variable.
 * If the length of a evaluated string expression exceeds 255 characters, the string is truncated on the right and the
 * program continues. No warning is given.
 */
class LetStringStatement(varName: String, override val expr: StringExpr) : LetStatement(varName) {
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        machine.setStringVariable(varName, expr)
    }
}

/**
 * The END statement ends your program and may be used interchangeably with the [StopStatement]. Although the END
 * statement may appear anywhere, it is normally placed as the last line in a program and thus ends the program
 * both physically and logically. The STOP statement is usually used if you want to have other termination points
 * in your program. In TI BASIC you are not required to place an end statement in your program.
 */
class EndStatement : Statement {
    override fun listText() = "END"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        machine.endProgramRun()
    }
}

/**
 * The STOP statement terminates your program when it is being run and can be used interchangeably with the
 * [EndStatement] in TI BASIC. You can place STOP statements anywhere in your program and use several STOP statements
 * in the same program. Many BASIC programmers use the END statement if there is only one ending point in the program.
 */
class StopStatement : Statement {
    override fun listText() = "STOP"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        machine.endProgramRun()
    }
}

/**
 * The REMark statement allows you to explain and document your program by inserting comments in the program
 * itself. When the computer encounters a remark statement while running your program, it takes no action but
 * proceeds to the next statement.
 */
class RemarkStatement(val text: String) : Statement {
    override fun listText(): String = "REM $text"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) = println("Remark: $text")
}

/**
 * The GOTO statement allows you to transfer control backward or forward within a program. Whenever the computer
 * reaches a GOTO statement, it will always jump to the statement with the specified line-number. This is called an
 * unconditional branch.
 * If you should tell the computer to skip to a line-number that does not exist in your program, the program will stop
 * running and print the message "BAD LINE NUMBER".
 * Note that the space between the words GO and TO is optional.
 */
class GoToStatement(originalLineNum: Int) : LineNumberDependentStatement {
    private var lineNumber: Int = originalLineNum

    override fun listText(): String = "GO TO $lineNumber" // TODO: Implement GOTO variant
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        val interpreter =
            machine.programInterpreter ?: throw IllegalArgumentException("GO TO must not be used without program")
        interpreter.jumpTo(lineNumber)
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

class IfStatement(private val numericExpr: NumericExpr, val lineNumber1: Int) : Statement {
    override fun listText(): String = "IF ${numericExpr.listText()} THEN $lineNumber1"

    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        val currVal = numericExpr.value()
        val isTrue = currVal.toNative() != 0.0
        if (isTrue) machine.programInterpreter!!.jumpTo(lineNumber1)
    }
}