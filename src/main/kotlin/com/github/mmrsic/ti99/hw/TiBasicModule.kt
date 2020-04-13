package com.github.mmrsic.ti99.hw

import com.github.mmrsic.ti99.basic.*
import com.github.mmrsic.ti99.basic.expr.Constant
import com.github.mmrsic.ti99.basic.expr.Expression
import com.github.mmrsic.ti99.basic.expr.NumericConstant
import com.github.mmrsic.ti99.basic.expr.NumericExpr
import com.github.mmrsic.ti99.basic.expr.NumericVariable
import com.github.mmrsic.ti99.basic.expr.PrintSeparator
import com.github.mmrsic.ti99.basic.expr.StringConstant
import com.github.mmrsic.ti99.basic.expr.StringExpr
import com.github.mmrsic.ti99.basic.expr.TabFunction
import com.github.mmrsic.ti99.basic.expr.toAsciiCode
import java.util.TreeMap
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * A [TiModule] for TI Basic.
 */
class TiBasicModule : TiModule {

    /** A dependent of a [TiBasicModule]. */
    interface Dependent {
        /** The [TiBasicModule] this instance depends on. */
        val basicModule: TiBasicModule
    }

    /** Any instance that has to be executed when it is stored in a [TiBasicModule] */
    interface ExecutedOnStore {
        /** Notify this instance that it is stored in a given [TiBasicModule]. */
        fun onStore(lineNumber: Int, machine: TiBasicModule)
    }

    /** Sound producing component of this [TiBasicModule]. */
    val sound: TiSound = TiSoundDummy()

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

    /** All program line hooks */
    private val programLineHooks = mutableMapOf<(ProgramLine) -> Boolean, (ProgramLine) -> Unit>()

    private var currentPrintColumn: Int? = null

    private val stringVariables: MutableMap<String, StringConstant> = TreeMap()
    private val numericVariables: MutableMap<String, NumericConstant> = TreeMap()

    private val characterPatterns: MutableMap<Int, String> = TreeMap()

    /** TI Basic screen component as a 32x24 characters grid of 8x8 pixels. */
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

    /** Close any currently open files. */
    fun closeOpenFiles() {
        // TODO: Not yet implemented: Close open files
    }

    /** Release all space that had been allocated for special characters. */
    fun resetCharacters() {
        for (code in 32..127) characterPatterns.remove(code)
    }

    /** Reset all color sets to the standard colors. */
    fun resetColors() {
        screen.colors.reset()
    }

    /** Reset [getAllNumericVariableValues] and [getAllStringVariableValues] of this instance to an empty map. */
    fun resetVariables() {
        numericVariables.clear()
        stringVariables.clear()
        arrayLowerLimit = 0
    }

    /** Either [setStringVariable] or [setNumericVariable] depending on the variable name. */
    fun setVariable(variableName: String, value: String) {
        if (variableName.last() == '$') {
            setStringVariable(variableName, StringConstant(value))
        } else {
            val varValue = NumericConstant(value.toDouble())
            setNumericVariable(variableName, varValue)
        }
    }

    /**
     * Set a variable given as an [Expression] to a given string value. According to the resulting variable's name
     * the string value may be interpreted as numeric value.
     */
    fun setVariable(variableName: Variable, stringValue: String) {
        val memoryVarName = variableName.name
        setVariable(memoryVarName, stringValue.replace("\"\"", "\""))
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
        if (userFunctions.containsKey(name)) return evaluateUserFunction(name, listOf(), null) as NumericConstant
        if (!numericVariables.containsKey(name)) numericVariables[name] = NumericConstant.ZERO
        return numericVariables[name]!!
    }

    /** All the numeric variable names and their numeric constant values currently known by this instance. */
    fun getAllNumericVariableValues(): Map<String, NumericConstant> = numericVariables

    /** Change the value of a numeric variable of this instance. */
    fun setNumericVariable(name: String, expr: NumericExpr): NumericConstant {
        if (name.length > 15) throw BadName()
        val originalValue = expr.value()
        if (originalValue.isOverflow) {
            numericVariables[name] = NumericConstant(originalValue.toNative())
            throw NumberTooBig()
        }
        val result = if (originalValue.isUnderflow) NumericConstant.ZERO else originalValue
        numericVariables[name] = result
        println("$name=$result")
        return result
    }

    /** The array variable value for a given variable name and index expression. */
    fun getNumericArrayVariableValue(name: String, indexList: List<NumericExpr>): NumericConstant {
        if (name.isEmpty() || name.last() == '$') throw IllegalArgumentException("Illegal name: '$name'")
        if (userFunctions.containsKey(name)) {
            val result = evaluateUserFunction(name, indexList, null)
            if (result !is NumericConstant) throw IllegalArgumentException("Not a numeric user defined function: $name = $result")
            return result
        }
        return getNumericVariableValue(getArrayVariableName(name, indexList))
    }

    /**
     * Set the value of a numeric array variable to a given numeric constant.
     * @param name Base name of the array to set, that is the A of the A(1,2)=value
     * @param indices all the indices of the array, that is, the (1,2) of A(1,2)=value
     * @param value the new value of the numeric array variable, that is, the value of A(1,2)=value
     */
    fun setNumericArrayVariable(name: String, indices: List<NumericExpr>, value: NumericConstant) {
        setNumericVariable(getArrayVariableName(name, indices), value)
    }

    /** The name of an array variable for given base name and index expressions. */
    fun getArrayVariableName(baseName: String, indexExpressions: List<NumericExpr>): String {
        if (indexExpressions.isEmpty()) throw IllegalArgumentException("Array index must not be empty")
        return StringBuilder().apply {
            append(baseName)
            for (indexExpr in indexExpressions) {
                val index = indexExpr.value().toNative().roundToInt()
                if (index == 0 && arrayLowerLimit > 0) throw BadSubscript()
                append("-$index")
            }
        }.toString()
    }

    private var arrayLowerLimit: Int = 0

    /** Set the lower limit of all array subscripts of this TI Basic module to either zero or one. */
    fun setArrayLowerLimit(lowerLimit: Int) {
        if (lowerLimit !in 0..1) throw IllegalArgumentException("Illegal lower limit for array subscripts: $lowerLimit")
        arrayLowerLimit = lowerLimit
    }

    /** Helper class to represent user defined functions. */
    private data class UserFunction(val parameterName: String?, val definition: Expression)

    /** All user functions currently defined within this [TiBasicModule]. */
    private val userFunctions: MutableMap<String, UserFunction> = mutableMapOf()

    /** All user functions currently being executed. */
    private val executedUserFunctions: MutableSet<String> = mutableSetOf()

    /** Define a user function for given function name, optional parameter name, and function definition. */
    fun defineUserFunction(functionName: String, parameterName: String?, definition: Expression) {
        if (functionName.last() == '$' != definition is StringExpr)
            throw IllegalArgumentException("Function name '$functionName' must match definition type: $definition")
        userFunctions[functionName] = UserFunction(parameterName, definition)
    }

    /** Evaluate a user function for a given user function name and an optional parameter. */
    fun evaluateUserFunction(name: String, parameterList: List<Expression>, programLineNumber: Int?): Constant {
        val userFunction = userFunctions[name] ?: throw IllegalArgumentException("No such user function: $name")
        if (userFunction.parameterName != null && parameterList.isEmpty()) {
            println("User function $name requires a parameter")
            throw NameConflict()
        } else if (userFunction.parameterName == null && parameterList.isNotEmpty()) {
            println("User function $name has no parameter: $parameterList")
            throw NameConflict()
        }

        if (executedUserFunctions.contains(name)) {
            println("User function recursion: $name")
            throw MemoryFull()
        }
        executedUserFunctions.add(name)
        val hiddenValues: MutableList<Constant> = mutableListOf()
        for (parameterExpr in parameterList) {
            if (parameterExpr is NumericExpr) {
                hiddenValues.add(getNumericVariableValue(userFunction.parameterName!!))
                setNumericVariable(userFunction.parameterName, parameterExpr)
            } else if (parameterExpr is StringExpr) {
                hiddenValues.add(getStringVariableValue(userFunction.parameterName!!))
                setStringVariable(userFunction.parameterName, parameterExpr)
            }
        }
        println("Evaluating $userFunction for parameter value $parameterList [hidden values=$hiddenValues]")
        val result = evaluateRuntimeExpression(userFunction.definition, programLineNumber)
        for (hiddenValue in hiddenValues) {
            if (hiddenValue is NumericExpr) setNumericVariable(userFunction.parameterName!!, hiddenValue)
            else if (hiddenValue is StringExpr) setStringVariable(userFunction.parameterName!!, hiddenValue)
        }
        executedUserFunctions.remove(name)
        return result
    }

    /**
     * Check whether a already defined user function given by its name has a parameter name conflicting with the
     * variables used in its definition.
     * @return true if the specified function name bears a name conflict, false otherwise
     */
    fun hasUserFunctionParameterNameConflict(functionName: String): Boolean {
        val userFunctionToCheck =
            userFunctions[functionName] ?: throw IllegalArgumentException("No such user function: $functionName")
        val arg = userFunctionToCheck.parameterName
        return userFunctionToCheck.definition.listText().contains(Regex("\\b$arg\\("))
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
        programLine.statements.forEach { if (it is ExecutedOnStore) it.onStore(programLine.lineNumber, this) }
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
        if (startLine != null && programToRun != null && !programToRun.hasLineNumber(startLine)) throw BadLineNumber()
        resetCharacters()
        resetVariables()
        interpretProgram(startLine)
    }

    /** Unconditionally stop the current program run. Has no effect, if no program is running. */
    fun endProgramRun() {
        programInterpreter = null
        resetCharacters()
        executedUserFunctions.clear()
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

    /**
     * Add a given code to be executed after execution of a program line for which a given line filter applies.
     * @param lineFilter executed after each program line, defining whether to execute the specified hook code
     * @param hookCode executed for every program line for which the specified filter returned true
     */
    fun addProgramLineHookAfter(lineFilter: (ProgramLine) -> Boolean, hookCode: (ProgramLine) -> Unit) {
        programLineHooks[lineFilter] = hookCode
    }

    /**
     * Add a given code to be executed after execution of a program line with a given line number.
     * @param lineNumber existing line number of the program executed
     * @param hookCode executed after the program line with the specified line number
     */
    fun addProgramLineHookAfterLine(lineNumber: Int, hookCode: (ProgramLine) -> Unit) {
        addProgramLineHookAfter({ programLine -> programLine.lineNumber == lineNumber }, hookCode)
    }

    internal fun programLineExecutionComplete(programLineNumber: Int) {
        program!!.withProgramLineNumberDo(programLineNumber) { programLine ->
            programLineHooks.entries.forEach {
                if (it.key.invoke(programLine)) it.value.invoke(programLine)
            }
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

    /** Set the color of any of the available character sets to given foreground and background colors. */
    fun setColor(characterSet: NumericConstant, foreground: NumericConstant, background: NumericConstant) {
        val charSetNumber = characterSet.value().toNative().roundToInt()
        val fCode = foreground.value().toNative().roundToInt()
        val bCode = background.value().toNative().roundToInt()
        val charSetColors = TiCharacterColor(TiColor.fromCode(fCode), TiColor.fromCode(bCode))
        screen.colors.setCharacterSet(charSetNumber, charSetColors)
    }

    /** Read the code of the character currently displayed at the [screen] of this module. */
    fun readScreenCharCode(numericVarName: String, row: Int, column: Int): Int {
        val charCode = screen.codes.codeAt(row, column)
        setNumericVariable(numericVarName, NumericConstant(charCode))
        return charCode
    }

    /** Print a given list of tokens onto the screen. */
    fun printTokens(tokens: List<Expression>, programLineNumber: Int? = null) {
        val minCol = TiBasicScreen.FIRST_PRINT_COLUMN
        val maxCol = TiBasicScreen.LAST_PRINT_COLUMN
        val rightHalfMinCol = (TiBasicScreen.NUM_PRINT_COLUMNS / 2) + minCol
        val currRow = TiBasicScreen.NUM_ROWS
        var currCol = if (currentPrintColumn != null) currentPrintColumn!! else minCol
        currentPrintColumn = null
        for ((tokenIdx, token) in tokens.withIndex()) {
            if (tokenIdx == tokens.size - 1 && token == PrintSeparator.NextRecord) continue
            val tokenValue: Expression = when (token) {
                is TabFunction, is PrintSeparator -> token
                else -> evaluateRuntimeExpression(token, programLineNumber)
            }
            when (tokenValue) {
                PrintSeparator.Adjacent -> {
                    // Nothing to do
                }
                PrintSeparator.NextRecord -> {
                    screen.scroll(); currCol = minCol
                }
                PrintSeparator.NextField -> currCol = if (currCol < rightHalfMinCol) rightHalfMinCol else {
                    screen.scroll(); minCol
                }
                is TabFunction -> {
                    val newCol = minCol - 1 + tokenValue.value().toNative().toInt()
                    if (currCol > newCol) screen.scroll()
                    currCol = newCol
                }
                is NumericConstant, is StringConstant -> {
                    val exprString = tokenValue.displayValue()
                    val exprChars = when {
                        token is NumericExpr && currCol + exprString.length == maxCol + 2 -> exprString.dropLast(1)
                        else -> exprString
                    }
                    if (currCol > minCol && currCol + exprChars.length > maxCol + 1) {
                        screen.scroll(); currCol = minCol
                    }
                    var leftOver = screen.hchar(currRow, currCol, exprChars, maxCol)
                    currCol += exprChars.length - leftOver.length
                    while (leftOver.isNotEmpty()) {
                        screen.scroll()
                        currCol = minCol
                        val last = leftOver
                        leftOver = screen.hchar(currRow, currCol, last, maxCol)
                        currCol += last.length
                    }
                }
                else -> println("Ignored in print statement: $token")
            }
        }
        val suppressScroll =
            listOf(PrintSeparator.Adjacent, PrintSeparator.NextField).contains(tokens.lastOrNull())
        if (suppressScroll) currentPrintColumn = currCol else screen.scroll()
    }

    /** Evaluate a given [Expression], optionally from within a given program line number. */
    private fun evaluateRuntimeExpression(expression: Expression, programLineNumber: Int?) =
        expression.value { intermediateValue ->
            if (intermediateValue is NumericConstant && intermediateValue.isOverflow) {
                screen.scroll()
                screen.print("* WARNING:")
                screen.print("  NUMBER TOO BIG" + if (programLineNumber != null) " IN $programLineNumber" else "")
            }
        }

    /** Current [KeyboardInputProvider] used by this module. */
    private var keyboardInputProvider: KeyboardInputProvider = object : KeyboardInputProvider {
    }

    /** Set the provider for keyboard input to a given instance. */
    fun setKeyboardInputProvider(newProvider: KeyboardInputProvider) {
        keyboardInputProvider = newProvider
    }

    /**
     * Accept user input via keyboard storing it in a variable given by its name.
     * @param variableNames names of the variables where to store the user's input - must end with a '$' for string variables
     * @param programLineNumber program line number used for programmatically provided user input
     * @param prompt screen text presented to the user when asking for input
     */
    fun acceptUserInput(variableNames: List<Variable>, programLineNumber: Int, prompt: String = "? ") {
        val interpreter = programInterpreter
            ?: throw IllegalArgumentException("User input is possible only while a program is running")
        interpreter.acceptUserInput(variableNames, programLineNumber, prompt)
        screen.scroll()
        currentPrintColumn = null
    }

    /** Accept a single keyboard key press from the TI 99/4a keyboard. */
    fun acceptKeyboardInput(
        programLineNumber: Int?,
        keyUnit: NumericConstant,
        returnVar: NumericVariable,
        statusVar: NumericVariable
    ) {
        val keyCode = keyboardInputProvider.currentlyPressedKeyCode(object : KeyboardInputProvider.CallKeyContext {
            override val programLineNumber = programLineNumber
            override val keyUnit = keyUnit.toNative().roundToInt()
        })
        if (keyCode == null) {
            setNumericVariable(statusVar.name, NumericConstant.ZERO)
            return
        }
        println("Keyboard codes: $keyCode")
        setNumericVariable(statusVar.name, NumericConstant.ONE) // TODO: May be -1 if same key is pressed again
        setNumericVariable(returnVar.name, NumericConstant(keyCode.code))
    }

    private val joysticks: MutableMap<Int, Joystick> = mutableMapOf()

    /** Plug-in a given [Joystick] as usable by this [TiBasicModule]. */
    fun plugInJoystick(joystick: Joystick) {
        joysticks[joystick.id] = joystick
        println("Plugged in: $joystick")
    }

    /** The [Joystick.State] given by a joystick's ID. */
    fun getJoystickState(id: NumericConstant): Joystick.State {
        val joystick = joysticks[id.toNative().roundToInt()]
        return joystick?.currentState() ?: Joystick.DefaultState
    }

    /** Data for a program. */
    private val programData = mutableMapOf<Int, List<Constant>>()

    /** Store some program data in memory to be used on program run. */
    fun storeData(lineNumber: Int, constants: List<Constant>) {
        programData[lineNumber] = constants
        println("Data @$lineNumber: $constants")
    }

    /** All currently opened files managed by this [TiBasicModule]. */
    private val openFiles: MutableMap<Int, TiBasicFile> = mutableMapOf()

    /** Open a file and associate it with a given file number. */
    fun openFile(fileNumber: NumericExpr, fileName: StringExpr, options: FileOpenOptions) {
        val number = fileNumber.value().toNative().roundToInt()
        val name = fileName.value().toNative()
        openFiles[number] = chooseAccessoryDevice(name).open(name, options)
    }

    /** Close and optionally delete a file given by its file number. */
    fun closeFile(fileNumber: NumericExpr, delete: Boolean) {
        val number = fileNumber.value().toNative().roundToInt()
        val file = openFiles[number] ?: throw FileError()
        file.close()
        if (delete) file.delete()
    }

    /** Read data from a file associated to a [fileNumber] into variables given by their [variableNames]. */
    fun readFromFile(fileNumber: NumericExpr, variableNames: List<String>) {
        val number = fileNumber.value().toNative().roundToInt()
        val file = openFiles[number] ?: throw IllegalArgumentException("No such file number: $number")
        for (variableName in variableNames) setVariable(variableName, file.getNextString())
    }

    private val attachedAccessoryDevices: MutableMap<String, AccessoryDevice> = mutableMapOf()

    /** Attach a cassette tape to this [TiBasicModule]. */
    fun attachCassetteTape(cassetteRecorderId: String, tapeDisplayData: String) {
        val recorder = chooseCassetteRecorder(cassetteRecorderId)
        recorder.insertTape(tapeDisplayData)
    }

    private fun chooseAccessoryDevice(deviceAndFileName: String): AccessoryDevice {
        if (attachedAccessoryDevices.containsKey(deviceAndFileName)) {
            return attachedAccessoryDevices.getValue(deviceAndFileName)
        }
        if (deviceAndFileName.startsWith("CS")) {
            val result = AccessoryDeviceCassetteRecorder(deviceAndFileName, this)
            attachedAccessoryDevices[deviceAndFileName] = result
            return result
        }
        return object : AccessoryDevice {}
    }

    private fun chooseCassetteRecorder(id: String): AccessoryDeviceCassetteRecorder {
        attachedAccessoryDevices.putIfAbsent(id, AccessoryDeviceCassetteRecorder(id, this))
        return attachedAccessoryDevices.getValue(id) as AccessoryDeviceCassetteRecorder
    }

    /** Provider of pseudo-random values. */
    private var randomProvider = PseudoRandomGenerator(0x6fe5, 0x7ab9).apply {
        PseudoRandomGenerator.Seed.value = 0x3567
    }

    /** Randomize the RND function. */
    fun randomize(seed: NumericConstant?) {
        if (seed != null) {
            PseudoRandomGenerator.Seed.value = 0x4000 + seed.toNative().roundToInt()
            println("Randomized random numbers with seed=$seed")
        } else {
            TODO("Randomize without seed not yet implemented")
        }
    }

    /** A random value between 0 (inclusive) and 1 (exclusive). */
    fun nextRandom(): Double {
        val randoms = mutableMapOf<Int, Int>()
        for (randTry in 63 downTo 1) {
            randoms[0] = randomProvider.nextRandom(100)
            if (randoms[0] != 0) break
        }
        if (randoms[0] == 0) {
            println("Pseudo random algorithm didn't produce a random greater than zero on 63 tries, returning zero")
            return 0.0
        }
        // Generate six additional random values
        for (randIdx in 1..6) randoms[randIdx] = randomProvider.nextRandom(100)
        val randomDecimal: String = StringBuilder(".").apply {
            for (digitsIdx in 0..6) append(randoms[digitsIdx].toString().padStart(2, '0'))
        }.toString()
        println("Generated random: $randomDecimal")
        return randomDecimal.toDouble()
    }

    // HELPERS //

    private fun interpretProgram(startLine: Int?) {
        currentPrintColumn = null // TODO: Move to program interpreter?
        val interpreter = TiBasicProgramInterpreter(this, keyboardInputProvider, programData)
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
            executedUserFunctions.clear()
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
            toAsciiCode('*') -> "000028107C102800"
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

/** Any (numeric or string) variable. */
interface Variable {
    /** Name of the variable. */
    val name: String
}