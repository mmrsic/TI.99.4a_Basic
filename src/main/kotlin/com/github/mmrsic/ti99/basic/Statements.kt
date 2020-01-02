package com.github.mmrsic.ti99.basic

import com.github.mmrsic.ti99.basic.expr.NumericExpr
import com.github.mmrsic.ti99.hw.TiBasicModule

interface Statement : TiBasicExecutable

class PrintStatement(private val expressions: List<Any>) : Statement, Command {

    override val name: String = "PRINT"

    override fun execute(machine: TiBasicModule) {
        var currRow = 24
        var currCol = 3
        for (expression in expressions) {
            if (expression is NumericExpr) {
                val characters = expression.displayValue()
                machine.screen.hchar(currRow, currCol, characters)
                currCol += characters.length
            } else if (expression is String) {
                machine.screen.hchar(currRow, currCol, expression)
                currCol += expression.length
            }
        }
        machine.screen.print("")
    }
}

class AssignNumberStatement(val varName: String, val expr: NumericExpr) : Statement {
    override fun execute(machine: TiBasicModule) = machine.setNumericVariable(varName, expr)
}

class EndStatement : Statement {
    override fun execute(machine: TiBasicModule) {
        // TODO: End current program run
    }
}