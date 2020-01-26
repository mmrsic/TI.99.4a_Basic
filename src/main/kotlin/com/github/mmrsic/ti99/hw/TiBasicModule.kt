package com.github.mmrsic.ti99.hw

import com.github.mmrsic.ti99.basic.*
import com.github.mmrsic.ti99.basic.expr.*
import java.util.*
import kotlin.math.max
import kotlin.math.min

class TiBasicModule : TiModule {
    var program: TiBasicProgram? = null
        private set
    /** Current breakpoints of this program. */
    private val breakpoints = HashSet<Int>()
    /** Last hit breakpoint. */
    private var continueLine: Int? = null

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
    fun cancelBreak() {
        println("Not yet implemented: Cancel BREAK")
    }

    /** Cancel the effect of the TRACE command. */
    fun cancelTrace() {
        println("Not yet implemented: Cancel TRACE")
    }

    /** Close any currently open files. */
    fun closeOpenFiles() {
        println("Not yet implemented: Close open files")
    }

    /** Release all space that had been allocated for special characters. */
    fun resetCharacters() {
        characterPatterns.clear() // TODO: Clear only the character patterns in the standard character set
    }

    /** Reset all color sets to the standard colors. */
    fun resetColors() {
        println("Not yet implemented: Reset colors")
    }

    /** Reset [getAllNumericVariableValues] and [getAllStringVariableValues] of this instance to an empty map. */
    fun resetVariables() {
        numericVariables.clear()
        stringVariables.clear()
    }

    /** The current value of a string value given by its name. */
    fun getStringVariableValue(name: String): StringConstant {
        if (name.last() != '$') throw IllegalArgumentException("Illegal string variable name: $name")

        if (name.length > 15) throw BadName()
        if (!stringVariables.containsKey(name)) stringVariables[name] = StringConstant((""))
        return stringVariables[name]!!
    }

    /** Change the value of a numeric variable of this instance.*/
    fun setStringVariable(name: String, expr: StringExpr) {
        if (name.length > 15) throw BadName()
        stringVariables[name] = StringConstant(expr.displayValue())
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
    fun setNumericVariable(name: String, expr: NumericExpr) {
        if (name.length > 15) {
            throw BadName()
        }
        numericVariables[name] = expr.value()
    }

    /** Initialize the [screen] of this module to the command interpreter mode after entering the */
    fun initCommandScreen() {
        screen.clear()
        screen.strings.displayAt(22, 3, "TI BASIC READY")
        screen.acceptAt(24, 2, ">")
    }

    /** Store a given [ProgramLine] into this instance's [program]. */
    fun store(programLine: ProgramLine) {
        if (program == null) {
            program = TiBasicProgram()
        }
        program!!.store(programLine)
        continueLine = null
    }

    /** Remove a given line number from the program of this module. */
    fun removeProgramLine(lineNumber: Int) {
        val programToChange = program
        if (programToChange == null) {
            return
        }
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

    fun runProgram(startLine: Int?) {
        val programToRun = program
        if (startLine != null && programToRun != null && !programToRun.hasLineNumber(startLine)) {
            throw BadLineNumber()
        }
        resetCharacters()
        resetVariables()
        interpretProgram(startLine)
    }

    /**
     * Set new breakpoints at given program lines of this program. Any previously present breakpoints will be
     * preserved.
     */
    fun setBreakpoints(lineNumbers: List<Int>) {
        breakpoints.addAll(lineNumbers)
    }

    /**
     *  Check whether a given line number is set in the breakpoints of this module, and removes it if it is present.
     *  @return true if the program should break at the specified line number, false otherwise
     */
    fun checkBreakpoint(lineNumber: Int): Boolean {
        val breakpointHit = breakpoints.remove(lineNumber)
        continueLine = if (breakpointHit) lineNumber else null
        if (breakpointHit) {
            resetCharacters()
            resetColors()
        }
        return breakpointHit
    }

    fun removeBreakpoints(lineNumbers: List<Int> = listOf()) {
        if (lineNumbers.isEmpty()) breakpoints.clear() else breakpoints.removeAll(lineNumbers)
    }

    /** Continue the program of this module after a breakpoint was hit. */
    fun continueProgram() {
        if (continueLine == null) throw CantContinue()
        interpretProgram(continueLine)
    }

    /** Define the character pattern of a given character code. */
    fun defineCharacter(characterCode: Int, patternIdentifier: String) {
        characterPatterns.put(characterCode, patternIdentifier)
    }

    /** Return the current pattern for a given character code. */
    fun getCharacterPattern(characterCode: Int): String {
        if (characterPatterns.containsKey(characterCode)) {
            return characterPatterns[characterCode]!!
        }
        return defaultCharacterPattern(characterCode)
    }

    // HELPERS //

    private fun interpretProgram(startLine: Int?) {
        val runResult = TiBasicProgramInterpreter(this).interpretAll(startLine)
        if (runResult == null) {
            screen.print("")
            screen.print("** DONE **")
            screen.print("")
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
