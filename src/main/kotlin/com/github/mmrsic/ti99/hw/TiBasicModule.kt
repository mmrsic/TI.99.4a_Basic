package com.github.mmrsic.ti99.hw

import com.github.mmrsic.ti99.basic.ProgramLine
import com.github.mmrsic.ti99.basic.TiBasicProgram
import com.github.mmrsic.ti99.basic.expr.NumericConstant
import com.github.mmrsic.ti99.basic.expr.NumericExpr
import java.util.*

class TiBasicModule {
    var program: TiBasicProgram? = null
        private set
    val screen = TiBasicScreen()
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
    }

    fun getNumericVariableValue(name: String): NumericConstant {
        return if (numericVariables.containsKey(name)) numericVariables[name]!! else NumericConstant(0)
    }

    fun getAllNumericVariableValues(): Map<String, NumericConstant> {
        return numericVariables
    }

    fun setNumericVariable(name: String, expr: NumericExpr) {
        numericVariables.putIfAbsent(name, NumericConstant(0))
        numericVariables[name] = expr.calculateToConstant()
    }

    fun initCommandScreen() {
        screen.clear()
        screen.strings.displayAt(22, 3, "TI BASIC READY")
        screen.acceptAt(24, 2, ">")
    }

    fun store(programLine: ProgramLine): Unit {
        if (program == null) {
            program = TiBasicProgram()
        }
        program!!.store(programLine)
    }

}
