package com.github.mmrsic.ti99.hw

import com.github.mmrsic.ti99.basic.*
import com.github.mmrsic.ti99.basic.expr.*
import java.util.*
import kotlin.math.max
import kotlin.math.min

class TiBasicModule : TiModule {

    /** The optional program currently held in this TI Basic Module's memory. */
    var program: TiBasicProgram? = null
        private set
    /** The optional [program] interpreter currently executing in this TI Basic Module's memory. */
    var programInterpreter: TiBasicProgramInterpreter? = null
        private set
    /** Whether or not tracing is active when the [programInterpreter] interprets a [program]. */
    var traceProgramExecution: Boolean = false

    /** Current breakpoints of this program. */
    private val breakpoints = HashSet<Int>()
    /** Last hit breakpoint. */
    private var continueLine: Int? = null

    private var currentPrintColumn: Int? = null

    private val stringVariables: MutableMap<String, StringConstant> = TreeMap()
    private val numericVariables: MutableMap<String, NumericConstant> = TreeMap()

    private val characterPatterns: MutableMap<Int, String> = TreeMap()

    val screen = TiBasicScreen { code -> getCharacterPattern(code) }

    init {
        enter()
    }

    override fun enter() = initCommandScreen()
    override fun leave() {
        closeOpenFiles()
        eraseProgram()
        resetVariables()
    }

    internal fun eraseProgram() {
        program = null
    }

    /** Cancel the effect of the BREAK command. */
    fun cancelBreak() = removeBreakpoints()

    /** Cancel the effect of the TRACE command. */
    fun cancelTrace() {
        // TODO: Not yet implemented: Cancel TRACE
    }

    /** Close any currently open files. */
    fun closeOpenFiles() {
        // TODO: Not yet implemented: Close open files
    }

    /** Release all space that had been allocated for special characters. */
    fun resetCharacters() {
        characterPatterns.clear() // TODO: Clear only the character patterns in the standard character set
    }

    /** Reset all color sets to the standard colors. */
    fun resetColors() {
        // TODO: Not yet implemented: Reset colors
    }

    /** Reset [getAllNumericVariableValues] and [getAllStringVariableValues] of this instance to an empty map. */
    fun resetVariables() {
        numericVariables.clear()
        stringVariables.clear()
    }

    /** Either [setStringVariable] or [setNumericVariable] depending on the variable name. */
    fun setVariable(variableName: String, value: String) {
        if (variableName.last() == '$') {
            setStringVariable(variableName, StringConstant(value))
        } else {
            setNumericVariable(variableName, NumericConstant(value.toDouble()))
        }
    }

    /** The current value of a string value given by its name. */
    fun getStringVariableValue(name: String): StringConstant {
        if (name.last() != '$') throw IllegalArgumentException("Illegal string variable name: $name")
        if (name.length > 15) throw BadName()
        if (!stringVariables.containsKey(name)) stringVariables[name] = StringConstant((""))
        return stringVariables[name]!!
    }

    /** Change the value of a numeric variable of this instance.*/
    fun setStringVariable(name: String, expr: StringExpr): StringConstant {
        if (name.length > 15) throw BadName()
        val result = StringConstant(expr.displayValue())
        stringVariables[name] = result
        println("$name=$result")
        return result
    }

    /** All the string variable names and their string constant values currently known by this instance. */
    fun getAllStringVariableValues(): Map<String, StringConstant> = stringVariables

    /** The current value of a numeric value given by its name. */
    fun getNumericVariableValue(name: String): NumericConstant {
        if (name.length > 15) throw BadName()
        if (!numericVariables.containsKey(name)) numericVariables[name] = NumericConstant(0)
        return numericVariables[name]!!
    }

    /** All the numeric variable names and their numeric constant values currently known by this instance. */
    fun getAllNumericVariableValues(): Map<String, NumericConstant> = numericVariables

    /** Change the value of a numeric variable of this instance. */
    fun setNumericVariable(name: String, expr: NumericExpr): NumericConstant {
        if (name.length > 15) throw BadName()
        val result = expr.value()
        numericVariables[name] = result
        println("$name=$result")
        return result
    }

    /** Initialize the [screen] of this module to the command interpreter mode after entering the */
    fun initCommandScreen() {
        screen.clear()
        screen.strings.displayAt(22, 3, "TI BASIC READY")
        screen.acceptAt(24, 2, ">")
    }

    /** Store a given [ProgramLine] into this instance's [program]. */
    fun store(programLine: ProgramLine) {
        checkLineNumber(programLine.lineNumber)
        closeOpenFiles()
        resetVariables()
        if (program == null) {
            program = TiBasicProgram()
        }
        program!!.store(programLine)
        continueLine = null
    }

    /** Remove a given line number from the program of this module. */
    fun removeProgramLine(lineNumber: Int) {
        val programToChange = program ?: return
        if (programToChange.remove(lineNumber)) continueLine = null
    }

    /** List the [program] of this instance. */
    fun listProgram(rangeStart: Int? = null, rangeEnd: Int? = null) {
        if (program == null) throw CantDoThat()
        if (rangeStart != null && (rangeStart == 0 || rangeStart > 32767)) throw BadLineNumber()
        if (rangeEnd != null && (rangeEnd == 0 || rangeEnd > 32767)) throw BadLineNumber()

        val programToList = program!!
        val firstLineNumber = programToList.firstLineNumber()
        val lastLineNumber = programToList.lastLineNumber()
        var currLineNum: Int? =
            if (rangeStart == null) firstLineNumber else min(max(firstLineNumber, rangeStart), lastLineNumber)
        if (!programToList.hasLineNumber(currLineNum!!)) {
            val lineNumToList = programToList.nextLineNumber(currLineNum)!!
            val statement = programToList.getStatements(lineNumToList)[0]
            screen.print("$lineNumToList ${statement.listText()}")
            return
        }
        while (currLineNum != null && (rangeEnd == null || currLineNum <= max(rangeEnd, firstLineNumber))) {
            val statement = programToList.getStatements(currLineNum)[0]
            screen.print("$currLineNum ${statement.listText()}")
            currLineNum = programToList.nextLineNumber(currLineNum)
        }
    }

    /** RESEQUENCE command for [program] of this instance. */
    fun resequenceProgram(initialLine: Int, increment: Int) {
        if (program == null) throw CantDoThat()
        program!!.resequence(initialLine, increment)
    }

    /** Run the [program] of this module, optionally starting at a given line number. */
    fun runProgram(startLine: Int? = null) {
        val programToRun = program
        if (startLine != null && programToRun != null && !programToRun.hasLineNumber(startLine)) {
            throw BadLineNumber()
        }
        resetCharacters()
        resetVariables()
        interpretProgram(startLine)
    }

    /** Unconditionally stop the current program run. Has no effect, if no program is running. */
    fun endProgramRun() {
        programInterpreter = null
    }

    /**
     * Add new breakpoints at given program lines of this program. Any previously present breakpoints will be
     * preserved.
     */
    fun addBreakpoints(lineNumbers: List<Int>, programLineNumber: Int? = null) {
        if (lineNumbers.any { !isCorrectLineNumber(it) }) throw BadLineNumber()
        for (lineNumber in lineNumbers) {
            try {
                addBreakpoint(lineNumber)
            } catch (e: TiBasicWarning) {
                if (programLineNumber != null) {
                    TiBasicProgramException(programLineNumber, e).displayOn(screen)
                } else {
                    e.displayOn(screen)
                }
            }
        }
    }

    /** Add a single breakpoint at a given program line number to the [program] of this module. */
    fun addBreakpoint(lineNumber: Int) {
        checkLineNumber(lineNumber)
        if (!program!!.hasLineNumber(lineNumber)) throw BadLineNumberWarning()
        breakpoints.add(lineNumber)
        println("Added new breakpoint at line $lineNumber")
    }

    /** Check whether a given line number is set in the breakpoints of this module. */
    fun hasBreakpoint(lineNumber: Int) = breakpoints.contains(lineNumber)

    /** Remove all breakpoints or breakpoints at a list of given line numbers.
     * @param lineNumbers if empty, all breakpoints are removed, otherwise only breakpoints at the specidied line
     * numbers are removed
     */
    fun removeBreakpoints(lineNumbers: List<Int> = listOf(), programLineNumber: Int? = null) {
        if (lineNumbers.any { !isCorrectLineNumber(it) }) throw BadLineNumber()
        if (lineNumbers.isEmpty()) {
            breakpoints.clear()
            println("Removed all breakpoints")
        } else {
            for (lineNumber in lineNumbers) {
                try {
                    removeBreakpoint(lineNumber)
                } catch (e: TiBasicWarning) {
                    if (programLineNumber != null) {
                        TiBasicProgramException(programLineNumber, e).displayOn(screen)
                    } else {
                        e.displayOn(screen)
                    }
                }
            }
            println("Removed breakpoints: $lineNumbers")
        }
    }

    /** Remove a single breakpoint at a given program line number from the [program] of this module. */
    fun removeBreakpoint(lineNumber: Int) {
        checkLineNumber(lineNumber)
        if (!program!!.hasLineNumber(lineNumber)) throw BadLineNumberWarning()
        breakpoints.remove(lineNumber)
        println("Removed breakpoint at line $lineNumber")
    }

    /** Continue the program of this module after a breakpoint was hit. */
    fun continueProgram() {
        val programToContinue = program ?: throw CantContinue()
        val lastBreakLine = continueLine ?: throw CantContinue()
        if (programToContinue.getStatements(lastBreakLine)[0] is SkippedOnContinue) {
            val lineAfterSkip = programToContinue.nextLineNumber(lastBreakLine)
            if (lineAfterSkip != null) interpretProgram(lineAfterSkip)
        } else {
            interpretProgram(lastBreakLine)
        }
    }

    /** Define the character pattern of a given character code. */
    fun defineCharacter(characterCode: Int, patternIdentifier: String) {
        characterPatterns[characterCode] = patternIdentifier
    }

    /** Return the current pattern for a given character code. */
    fun getCharacterPattern(characterCode: Int): String {
        if (characterPatterns.containsKey(characterCode)) {
            return characterPatterns[characterCode]!!
        }
        return defaultCharacterPattern(characterCode)
    }

    /** Print a given list of tokens onto the screen. */
    fun printTokens(expressions: List<Any>, programLineNumber: Int? = null) {
        var suppressScroll = false
        val maxCol = TiBasicScreen.MAX_COLUMNS - 2
        val currRow = 24
        var currCol = if (currentPrintColumn != null) currentPrintColumn!! else 3
        currentPrintColumn = null
        for ((exprIndex, expression) in expressions.withIndex()) {
            if (exprIndex == expressions.size - 1 && expression == PrintToken.NextRecord) continue
            if (expression is NumericExpr) {
                expression.visitAllValues { nc ->
                    if (nc.isOverflow) {
                        screen.scroll()
                        screen.print("* WARNING:")
                        screen.print("  NUMBER TOO BIG" + if (programLineNumber != null) " IN $programLineNumber" else "")
                    }
                }
            }
            suppressScroll = expression == PrintToken.Adjacent
            if (expression in PrintToken.values()) {
                when (expression) {
                    PrintToken.NextRecord -> {
                        screen.scroll(); currCol = 3
                    }
                }
            } else if (expression is Expression) {
                val characters = expression.displayValue()
                if (expression is NumericExpr && (currCol + characters.length) > maxCol + 1) {
                    screen.scroll(); currCol = 3
                }
                var lefOver = screen.hchar(currRow, currCol, characters, maxCol)
                currCol += characters.length - lefOver.length
                while (lefOver.isNotEmpty()) {
                    screen.scroll()
                    currCol = 3
                    val last = lefOver
                    lefOver = screen.hchar(currRow, currCol, last, maxCol)
                    currCol += lefOver.length - last.length
                }
            } else {
                println("Ignored in print statement: $expression")
            }
        }
        if (suppressScroll) {
            currentPrintColumn = currCol
        } else {
            screen.scroll()
        }
    }

    /** Current [CodeSequenceProvider] used by this module. */
    private var codeSequenceProvider: CodeSequenceProvider = object : CodeSequenceProvider {
    }

    /** Set the provider for keyboard input to a given instance. */
    fun setKeyboardInputProvider(newProvider: CodeSequenceProvider) {
        codeSequenceProvider = newProvider
    }

    /**
     * Accept user input via keyboard storing it in a variable given by its name.
     * @param variableNames names of the variables where to store the user's input - must end with a '$' for string variables
     * @param programLineNumber program line number used for programmatically provided user input
     * @param prompt screen text presented to the user when asking for input
     */
    fun acceptUserInput(variableNames: List<String>, programLineNumber: Int, prompt: String = "? ") {
        val interpreter = programInterpreter
            ?: throw IllegalArgumentException("User input is possible only while a program is running")
        printTokens(listOf(StringConstant(prompt), PrintToken.Adjacent))
        interpreter.acceptUserInput(variableNames, programLineNumber, prompt)
        screen.scroll()
        currentPrintColumn = null
    }

    // HELPERS //

    private fun interpretProgram(startLine: Int?) {
        currentPrintColumn = null // TODO: Move to program interpreter?
        val interpreter = TiBasicProgramInterpreter(this, codeSequenceProvider)
        programInterpreter = interpreter
        interpretProgram(interpreter, startLine)
        programInterpreter = null
    }

    private fun interpretProgram(interpreter: TiBasicProgramInterpreter, startLine: Int?) {
        try {
            interpreter.interpretAll(startLine)
            screen.scroll()
            screen.print("** DONE **")
            screen.scroll()
        } catch (e: TiBasicException) {
            if (e is TiBasicProgramException && e.delegate is Breakpoint) {
                breakpoints.remove(e.lineNumber)
                continueLine = e.lineNumber
                resetCharacters()
                resetColors()
            }
            e.displayOn(screen)
        }
    }

    private fun defaultCharacterPattern(code: Int): String {
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

}

/** Check whether a given line number is in the allowed range. */
fun isCorrectLineNumber(lineNumber: Int) = lineNumber in 1..32767

/**
 * Check whether a given line number is acceptable
 * @param lineNumber the line number to check
 * @throws BadLineNumber if the specified line number is not acceptable
 */
fun checkLineNumber(lineNumber: Int) {
    if (!isCorrectLineNumber(lineNumber)) throw BadLineNumber()
}
