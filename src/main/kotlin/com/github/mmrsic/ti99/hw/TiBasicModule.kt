package com.github.mmrsic.ti99.hw

import com.github.mmrsic.ti99.basic.BadName
import com.github.mmrsic.ti99.basic.CantDoThat
import com.github.mmrsic.ti99.basic.ProgramLine
import com.github.mmrsic.ti99.basic.TiBasicProgram
import com.github.mmrsic.ti99.basic.expr.NumericConstant
import com.github.mmrsic.ti99.basic.expr.NumericExpr
import com.github.mmrsic.ti99.basic.expr.StringConstant
import com.github.mmrsic.ti99.basic.expr.StringExpr
import java.util.*

class TiBasicModule {
    var program: TiBasicProgram? = null
        private set
    val screen = TiBasicScreen()
    private val stringVariables: MutableMap<String, StringConstant> = TreeMap()
    private val numericVariables: MutableMap<String, NumericConstant> = TreeMap()

    init {
        initCommandScreen()
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

    fun resetVariables() {
        numericVariables.clear()
        stringVariables.clear()
    }

    fun getStringVariableValue(name: String): StringConstant {
        if (name.length > 15) {
            throw BadName()
        }
        return if (stringVariables.containsKey(name)) stringVariables[name]!! else StringConstant("")
    }

    fun setStringVariable(name: String, expr: StringExpr) {
        if (name.length > 15) {
            throw BadName()
        }
        stringVariables[name] = StringConstant(expr.displayValue())
    }

    fun getNumericVariableValue(name: String): NumericConstant {
        if (name.length > 15) {
            throw BadName()
        }
        return if (numericVariables.containsKey(name)) numericVariables[name]!! else NumericConstant(0)
    }

    fun getAllNumericVariableValues(): Map<String, NumericConstant> {
        return numericVariables
    }

    fun setNumericVariable(name: String, expr: NumericExpr) {
        if (name.length > 15) {
            throw BadName()
        }
        numericVariables[name] = expr.value()
    }

    fun initCommandScreen() {
        screen.clear()
        screen.strings.displayAt(22, 3, "TI BASIC READY")
        screen.acceptAt(24, 2, ">")
    }

    fun store(programLine: ProgramLine) {
        if (program == null) {
            program = TiBasicProgram()
        }
        program!!.store(programLine)
    }

    fun listProgram() {
        if (program == null) {
            throw CantDoThat()
        }
        val programToList = program!!
        var lineNumber: Int? = programToList.firstLineNumber()
        while (lineNumber != null) {
            val statement = programToList.getStatements(lineNumber)[0]
            screen.print("$lineNumber ${statement.listText()}")
            lineNumber = programToList.nextLineNumber(lineNumber)
        }
    }

}
