package com.github.mmrsic.ti99.hw

import com.github.mmrsic.ti99.basic.*
import com.github.mmrsic.ti99.basic.expr.NumericConstant
import com.github.mmrsic.ti99.basic.expr.NumericExpr
import com.github.mmrsic.ti99.basic.expr.StringConstant
import com.github.mmrsic.ti99.basic.expr.StringExpr
import java.util.*
import kotlin.math.max
import kotlin.math.min

class TiBasicModule : TiModule {
    var program: TiBasicProgram? = null
        private set
    val screen = TiBasicScreen()
    private val stringVariables: MutableMap<String, StringConstant> = TreeMap()
    private val numericVariables: MutableMap<String, NumericConstant> = TreeMap()

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
        println("Not yet implemented: Reset characters")
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
    }

    /** List the [program] of this instance. */
    fun listProgram(rangeStart: Int? = null, rangeEnd: Int? = null) {
        if (program == null) {
            throw CantDoThat()
        }
        if (rangeStart != null && (rangeStart == 0 || rangeStart > 32767)) {
            throw BadLineNumber()
        }
        if (rangeEnd != null && (rangeEnd == 0 || rangeEnd > 32767)) {
            throw BadLineNumber()
        }
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

}
