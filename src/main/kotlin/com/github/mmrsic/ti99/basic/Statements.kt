package com.github.mmrsic.ti99.basic

import com.github.mmrsic.ti99.basic.expr.Constant
import com.github.mmrsic.ti99.basic.expr.Expression
import com.github.mmrsic.ti99.basic.expr.NumericExpr
import com.github.mmrsic.ti99.basic.expr.StringExpr
import com.github.mmrsic.ti99.hw.TiBasicModule
import kotlin.math.roundToInt

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
    /** Change the line numbers of this [Statement] for a given mapping from old to new line numbers. */
    fun changeLineNumbers(lineNumbersMapping: Map<Int, Int>)
}

/**
 * The BREAK statement without a line number causes the program to stop when it is encountered. The line at which the
 * program stops is called a breakpoint.
 * @param lineNumberList the line-number-list is optional when BREAK is used as a statement - when present, it causes
 * the program to stop immediately before the specified lines are executed
 */
class BreakStatement(private val lineNumberList: List<Int>? = null) : Statement, SkippedOnContinue {
    override fun listText() = if (lineNumberList != null) "BREAK $lineNumberList" else "BREAK"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        programLineNumber ?: throw IllegalArgumentException("Break statement may not be used without program")
        if (lineNumberList == null) throw Breakpoint()
        if (lineNumberList.isEmpty()) throw Breakpoint()
        machine.addBreakpoints(lineNumberList, programLineNumber)
    }
}

/**
 * The PRINT statement lets you print numbers and strings on the screen.
 * When the computer performs a PRINT statement, the values of the expressions in the print-list are displayed on the
 * screen in order from left to right, as specified by the print-separators and tab-functions.
 *
 * The DISPLAY statement is identical to the PRINT statement when you use it to print items on the screen. The DISPLAY
 * statement may not be used to write on any device except the screen.
 *
 * @param printList consists of print-items - [NumericExpr] and [StringExpr] as well as [TabFunction] - and
 * print-separators - the punctuation between print-items (commas, colons, and semicolons)
 */
class PrintStatement(val printList: List<Expression>) : Statement, Command {
    override val name: String = "PRINT"
    override fun listText(): String {
        if (printList.isEmpty()) return name
        return "$name " + printList.joinToString("") { it.listText() }
    }

    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        machine.printTokens(printList, programLineNumber)
    }
}

/**
 * The LET statement allows you to assign values to variables in your program. The computer evaluates the expression
 * to the right of the equals sign and puts its value into the variable specified to the left of the equals sign.
 * @param varName name of the variable for which to assign the [expr]
 */
abstract class LetStatement(val varName: String) : Statement {
    /** The right-hand-side expression of this LET statement. */
    abstract val expr: Expression
    override fun listText(): String = "$varName=${expr.listText().trim()}" // TODO: Add optional LET
}

/**
 * [LetStatement] where a [NumericExpr] is assigned to a numeric variable.
 * The rules governing underflow and overflow for the evaluation of numeric expressions are used in the LET statement.
 */
open class LetNumberStatement(varName: String, override val expr: NumericExpr) : LetStatement(varName) {
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        machine.setNumericVariable(varName, expr)
    }
}

class LetNumberArrayElementStatement(baseVarName: String, val subscripts: List<NumericExpr>, expr: NumericExpr) :
    LetNumberStatement(baseVarName, expr) {
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        machine.setNumericArrayVariable(varName, subscripts, expr.value())
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
 * @param text text of the REMark statement
 */
class RemarkStatement(val text: String) : Statement {
    override fun listText(): String = "REM $text"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) = println("Remark: $text")
}

/**
 * The OPTION BASE statement allows you to set the [lowerLimit] of array subscripts at one instead of the default zero.
 * If you include it in your program, you must give it a lower line number than any [DimStatement] or any reference to
 * an element in any array. You may have only one OPTION BASE statement in a program, and it applies to all array
 * subscripts.
 * @param lowerLimit either 0 or 1
 */
class OptionBaseStatement(val lowerLimit: Int) : Statement {
    init {
        if (lowerLimit !in 0..1) throw IncorrectStatement()
    }

    override fun listText() = "OPTION BASE $lowerLimit"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        machine.setArrayLowerLimit(lowerLimit)
    }
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
        lineNumber = lineNumbersMapping.getValue(lineNumber)
    }
}

/**
 * The ON-GOTO statement tells the computer to jump to one of several program lines, depending on the value of the
 * numeric expression.
 */
class OnGotoStatement(val numericExpr: NumericExpr, val lineNumberList: List<Int>) : LineNumberDependentStatement {

    override fun listText() = "ON ${numericExpr.listText()} GOTO $lineNumberList"

    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        val interpreter =
            machine.programInterpreter ?: throw IllegalArgumentException("ON GOTO must not be used without program")
        val lineNumberIdx = numericExpr.value().toNative().roundToInt()
        if (lineNumberIdx !in 1..lineNumberList.size) throw BadValue()
        interpreter.jumpTo(lineNumberList[lineNumberIdx - 1])
    }

    override fun changeLineNumbers(lineNumbersMapping: Map<Int, Int>) = TODO("not implemented")
}

/**
 * The GOSUB statement is used with the [ReturnStatement] to allow you to transfer the program to a subroutine, complete
 * the steps in the subroutine, and return to the next program line following the GOSUB statement.
 * The space between GO and SUB is optional.
 * @param subprogramLineNumber program line number where to start the subroutine
 */
class GosubStatement(val subprogramLineNumber: Int) : LineNumberDependentStatement {
    override fun listText() = "GOSUB $subprogramLineNumber"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        val interpreter = machine.programInterpreter
            ?: throw IllegalArgumentException("GOSUB can be used only within a program")
        interpreter.gosub(subprogramLineNumber, programLineNumber!!)
    }

    override fun changeLineNumbers(lineNumbersMapping: Map<Int, Int>) {
        TODO("not implemented")
    }
}

/**
 * The ON-GOSUB statement is used with the [ReturnStatement] to tell the computer to perform one of several subroutines,
 * depending on the value of a [numericExpr], and then go back to the main program sequence.
 *
 * @param numericExpr evaluated and converted to an integer, rounded if necessary - must be between 1 and the number of
 * elements provided in [lineNumberList]
 * @param lineNumberList program line numbers for subroutines containing a [ReturnStatement]
 * @throws BadValue if [numericExpr] is less than 1 or greater than the number if line numbers
 * @throws BadLineNumber if the chosen line number from the specified list is no valid program line number
 */
class OnGosubStatement(val numericExpr: NumericExpr, val lineNumberList: List<Int>) : LineNumberDependentStatement {

    init {
        if (lineNumberList.isEmpty()) throw IllegalArgumentException("Line number must not be empty")
    }

    override fun listText() = "ON ${numericExpr.listText()} GOSUB $lineNumberList"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        val interpreter =
            machine.programInterpreter ?: throw IllegalArgumentException("ON GOSUB must not be used without program")
        val lineNumberIdx = numericExpr.value().toNative().roundToInt()
        if (lineNumberIdx !in 1..lineNumberList.size) throw BadValue()
        val currLineNumber = programLineNumber
            ?: throw IllegalArgumentException("ON GOSUB must not be used without current program line number")
        interpreter.gosub(lineNumberList[lineNumberIdx - 1], currLineNumber)
    }

    override fun changeLineNumbers(lineNumbersMapping: Map<Int, Int>) = TODO("not implemented")
}

/**
 * The RETURN statement is used with the [GosubStatement] to provide a branch and return structure for TI BASIC.
 * Whenever the computer encouters a RETURN statement, it takes the program back to the program line immediately
 * following the GOSUB statement that transferred the computer to that particular subroutine in the first place.
 */
class ReturnStatement : Statement {
    override fun listText() = "RETURN"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        val interpreter = machine.programInterpreter
            ?: throw IllegalArgumentException("RETURN can be used only within a program")
        interpreter.returnFromGosub()
    }
}

class UnbreakStatement(private val lineNumberList: List<Int>? = null) : LineNumberDependentStatement {
    override fun listText() = if (lineNumberList != null) "UNBREAK $lineNumberList" else "UNBREAK"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        programLineNumber ?: throw IllegalArgumentException("Unbreak statement may not be used without program")
        if (lineNumberList == null) {
            machine.removeBreakpoints(programLineNumber = programLineNumber)
        } else {
            machine.removeBreakpoints(lineNumberList, programLineNumber)
        }
    }

    override fun changeLineNumbers(lineNumbersMapping: Map<Int, Int>) {
        TODO("not yet implemented")
    }
}

/**
 * The FOR-TO-STEP statement is used for easy programming of repetitive (iterative) processes. Together with the
 * [NextStatement], the FOR-TO-STEP statement is used to construct a FOR-NEXT loop. If the STEP clause is omitted,
 * the computer uses an increment of +1.
 * The limit, and, optionally, the increment are numeric expressions that are evaluated once during a loop performance
 * and remain in effect until the loop is finished. Any change made to these values while a loop is in progress has no
 * effect on the number of times the loop is performed.
 */
class ForToStepStatement(val initializer: LetNumberStatement, val limit: NumericExpr, val increment: NumericExpr?) :
    Statement {

    override fun listText(): String {
        return when (increment) {
            null -> "FOR ${initializer.listText().trim()} TO ${limit.listText()}"
            else -> "FOR ${initializer.listText().trim()} TO ${limit.listText()} STEP ${increment.listText()}"
        }
    }

    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        val pln = programLineNumber ?: throw IllegalArgumentException("$this can be used as a statement only")
        val interpreter = machine.programInterpreter
            ?: throw IllegalArgumentException("Machine program interpreter must be present for $this")
        // Limit and increment must be evaluated before the initializer is executed!
        val limitConst = limit.value()
        val incrementConst = increment?.value()
        initializer.execute(machine, programLineNumber)
        interpreter.beginForLoop(pln, initializer.varName, limitConst, incrementConst)
    }

    override fun requiresEmptyLineAfterExecution() = false
}

/**
 * The NEXT statement is always paired with the [ForToStepStatement] for construction of a loop. The control-variable is
 * the same one that appears in the corresponding FOR-TO-STEP statement.
 * @param ctrlVarName name of the control-variable used in the for loop
 */
class NextStatement(val ctrlVarName: String) : Statement {
    override fun listText() = "NEXT $ctrlVarName"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        val interpreter =
            machine.programInterpreter ?: throw IllegalArgumentException("Cannot use $this without program")
        interpreter.nextForLoopStep(ctrlVarName)
    }

    override fun requiresEmptyLineAfterExecution() = false
}

/**
 * The IF-THEN-ELSE statement allows you to change the normal sequence of your program execution by using a conditional
 * branch. A value of 0 is treated as false, and any other value is treated as true. Thus, you can use multiplication
 * as a logical-AND and addition as logical-OR. If the expression is true, the computer will jump to [line1], which
 * follows the word THEN. If the condition is false, the computer will jump to [line2] following the word ELSE. If ELSE
 * is omitted, the computer continues with the next program line.
 */
class IfStatement(private val numericExpr: NumericExpr, val line1: Int, val line2: Int? = null) :
    LineNumberDependentStatement {

    override fun listText(): String = when {
        line2 != null -> "IF ${numericExpr.listText()} THEN $line1 ELSE $line2"
        else -> "IF ${numericExpr.listText()} THEN $line1"
    }

    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        val interpreter = machine.programInterpreter
        if (programLineNumber == null || interpreter == null) {
            throw IllegalArgumentException("Cannot use IF-THEN-ELSE without program")
        }
        val currVal = numericExpr.value()
        val isTrue = currVal.toNative() != 0.0
        if (isTrue) interpreter.jumpTo(line1)
        else if (line2 != null) interpreter.jumpTo(line2)
    }

    override fun changeLineNumbers(lineNumbersMapping: Map<Int, Int>) {
        TODO("not implemented")
    }
}

/**
 * This form of the input statement is used when entering data via the keyboard. The INPUT statement causes the program
 * to pause until valid data is entered from the keyboard.
 * @param promptExpr optional [StringExpr] that indicates on the screen the values you should enter at that time
 * @param varNameList contains those variable names which are assigned values when the INPUT statement is performed
 */
class InputStatement(val promptExpr: StringExpr?, val varNameList: List<Expression>) : Statement {

    override fun listText() = when (promptExpr) {
        null -> "INPUT ${varNameList.joinToString(",")}"
        else -> "INPUT $promptExpr:${varNameList.joinToString(",")}"
    }

    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        if (programLineNumber == null) throw IllegalArgumentException("Input statement must be used within program")
        if (promptExpr != null) {
            machine.acceptUserInput(varNameList, programLineNumber, promptExpr.value().toNative())
        } else {
            machine.acceptUserInput(varNameList, programLineNumber)
        }
    }
}

/**
 * The DIMension statement reserves space for both numeric and string arrays. You can explicitly dimension an array only
 * once in your program. If you dimension an array, the DIM statement must appear in the program before any other
 * reference to the array.
 */
class DimStatement(val arrayDeclarations: List<ArrayDimensions>) : Statement {
    init {
        if (arrayDeclarations.isEmpty()) throw IllegalArgumentException("Array declarations must not be empty")
    }

    override fun listText(): String {
        return StringBuilder().apply {
            append("DIM ")
            for (arrayDeclaration in arrayDeclarations) {
                if (arrayDeclaration !== arrayDeclarations[0]) append(',')
                append(arrayDeclaration)
            }
        }.toString()
    }

    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        // Nothing to do
    }

    /** Declaration of an TI Basic array with one to three dimensions. */
    data class ArrayDimensions(val name: String, val firstDim: Int, val secondDim: Int?, val thirdDim: Int?) {
        override fun toString(): String {
            return StringBuilder("$name($firstDim").apply {
                if (secondDim != null) append(secondDim)
                if (thirdDim != null) append(thirdDim)
                append(')')
            }.toString()
        }
    }
}

/**
 * The DATA statement allows you to store data inside your program. Data in the data-lists are obtained via
 * [ReadStatement]s when the program is run.
 * When a program reaches a DATA statement, it proceeds to the next statement with no other effect.
 * DATA statements may appear anywhere in a program, but the order in which they appear is important. Data from the
 * data-lists are read sequentially, beginning with the first item in the first DATA statement. If your program includes
 * more than one DATA statement, the DATA statements are read in ascending line-number order unless otherwise specified
 * by a [RestoreStatement]. Thus, the order in which the data appears within the data-list and the order of the DATA
 * statements within the program normally determine in which order the data is read.
 * @param dataList The data-list contains the values to be assigned to the variables
 * specified in the variable-list of the read statement.
 */
class DataStatement(val dataList: List<Constant>) : Statement, TiBasicModule.ExecutedOnStore {
    override fun listText() = "DATA $dataList"
    override fun onStore(lineNumber: Int, machine: TiBasicModule) {
        machine.storeData(lineNumber, dataList)
    }

    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        // Nothing to do: Everything is done on store
    }
}

/**
 * The READ statement allows you to read data stored inside your program in [DataStatement]s. The variable-list
 * specifies those variables that are to have values assigned.
 * @param variableList may include numeric variables and/or string variables
 */
class ReadStatement(val variableList: List<Expression>) : Statement {
    override fun listText() = "READ $variableList"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        val interpreter =
            machine.programInterpreter ?: throw IllegalArgumentException("$this must be called from within a program")
        interpreter.readData(variableList)
    }
}

/**
 * The RESTORE statement tells your program which [DataStatement] to use with the next [ReadStatement].
 * When RESTORE is used with no line number and the next [ReadStatement] is performed, values will be assigned beginning
 * with the first [DataStatement] in the program.
 * When RESTORE is followed by the line number of a [DataStatement] and the next [ReadStatement] is performed, values
 * will be assigned beginning with the first data-item in the [DataStatement] specified by the line number.
 * If the line number specified in a RESTORE statement is not a [DataStatement] or is not a program line number, then
 * the next [ReadStatement] performed will start at the first [DataStatement] whose line number is greater than the one
 * specified. If there is no such [DataStatement], then the next [ReadStatement] performed will cause an out-of-data
 * condition and a "DATA ERROR" message will be displayed. If the line number specified is greater than the highest
 * line number in the program, the program will stop running and the message "DATA ERROR IN xx" will be displayed.
 */
class RestoreStatement(val lineNumber: Int? = null) : LineNumberDependentStatement {
    override fun listText() = if (lineNumber != null) "RESTORE $lineNumber" else "RESTORE"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        val interpreter =
            machine.programInterpreter ?: throw IllegalArgumentException("$this must be called from within a program")
        interpreter.restore(lineNumber)
    }

    override fun changeLineNumbers(lineNumbersMapping: Map<Int, Int>) {
        TODO("not implemented")
    }
}

/**
 * The RANDOMIZE statement resets the random number generator to an unpredictable sequence. if RANDOMIZE is followed by
 * a numeric-expression, the same sequence of random numbers is produced each time the statement is executed with that
 * value for the expression. Different values give different sequences.
 */
class RandomizeStatement(private val seed: NumericExpr?) : Statement {
    override fun listText() = if (seed != null) "RANDOMIZE ${seed.listText()}" else "RANDOMIZE"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        machine.randomize(seed?.value())
    }
}

/**
 * The DEFine statement allows you to define your own functions. [functionName] may be any valid variable name. If you
 * specify a [parameterName], it may be any valid variable name. Note that if the [definition] you specify evaluates to
 * a string result, the function name you use must be a string variable name (i.e. the last character must be a dollar
 * sign, $).
 */
class DefineFunctionStatement(
    private val functionName: String,
    private val parameterName: String?,
    private val definition: Expression
) : Statement, TiBasicModule.ExecutedOnStore {

    override fun listText(): String {
        val arg = if (parameterName != null) "($parameterName)" else ""
        return "DEF $functionName$arg=${definition.listText()}"
    }

    override fun onStore(lineNumber: Int, machine: TiBasicModule) {
        machine.defineUserFunction(functionName, parameterName, definition)
    }

    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        if (machine.hasUserFunctionParameterNameConflict(functionName)) throw NameConflict()
    }
}

/**
 * The OPEN statement prepares a Basic program to use data files stored on accessory devices. It provides the necessary
 * link between a [fileNumber] used in your program and the particular accessory device on which the file is located.
 * If you define a file as [FileOrganization.Type.RELATIVE], you must use [RecordType.LengthType.FIXED] records. If
 * records are fixed, each record is padded on the right side: Spaces for DISPLAY format and binary zeros for INTERNAL
 * format.
 *
 * @param fileNumber a value between 0 and 255 inclusive - since file number 0 refers to the keyboard and screen of the
 * computer and is always accessible, you cannot open or close it in your program statements - each open file in the
 * program must have a different number
 * @param fileName a file name refers to a device or to a file located on a device, depending on the capability of the
 * accessory - each accessory has a predefined name which the computer recognizes, for example, the valid file names for
 * the two audio cassette recorders are "CS1" and "CS2"
 * @param fileOrganization used to indicate, which logical structure a file has - DEFAULT is SEQUENTIAL
 * @param fileType designates the format of the data stored on the file: DISPLAY or INTERNAL - default is DISPLAY
 * @param openMode instructs the computer to process the file in the [OpenMode.INPUT], [OpenMode.OUTPUT],
 * [OpenMode.UPDATE], or [OpenMode.APPEND] mode - default is [OpenMode.UPDATE]
 * @param recordType specifies whether the records on the file are all the same length or vary in length - may include a
 * numeric expression specifying the maximum length of a record - each accessory device has its own maximum record
 * length, so be sure to check the manuals which accompany them - if omitted, a record length depending upon the device
 * is used: SEQUENTIAL for VARIABLE-length files, RELATIVE for FIXED-length files
 */
class OpenStatement(val fileNumber: NumericExpr, val fileName: StringExpr, val options: FileOpenOptions) : Statement {

    override fun listText(): String {
        TODO("not implemented")
    }

    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        machine.openFile(fileNumber, fileName, options)
    }

}

/**
 * The CLOSE statement "closes" or discontinues the association between a file and a program. After the CLOSE statement
 * is performed, the "closed" file is not available to your program unless you OPEN it again. Also, the computer will
 * no longer associate the closed file with the [fileNumber] you specified in the program. You can then assign that
 * particular [fileNumber] to any file you wish.
 * If you attempt to CLOSE a file that you have not opened previously in your program, the computer will terminate your
 * program with the FILE ERROR message.
 * @param fileNumber any number previously used in the [OpenStatement]
 * @param delete if you use the DELETE option in the CLOSE statement, the action performed depends in the device used
 */
class CloseStatement(val fileNumber: NumericExpr, val delete: Boolean = false) : Statement {

    override fun listText(): String {
        return StringBuilder("CLOSE").apply {
            append("# ").append(fileNumber.value().toNative().roundToInt())
            if (delete) append(":DELETE")
        }.toString()
    }

    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        machine.closeFile(fileNumber, delete)
    }

}