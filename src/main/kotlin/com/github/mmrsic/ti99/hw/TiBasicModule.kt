package com.github.mmrsic.ti99.hw

import com.github.mmrsic.ti99.basic.BadName
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

    fun cancelBreak() {
    }

    fun cancelTrace() {
    }

    fun closeOpenFiles() {
    }

    fun resetCharacters() {
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

}
