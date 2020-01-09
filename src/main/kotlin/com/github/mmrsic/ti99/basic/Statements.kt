package com.github.mmrsic.ti99.basic

import com.github.mmrsic.ti99.basic.expr.Expression
import com.github.mmrsic.ti99.basic.expr.NumericExpr
import com.github.mmrsic.ti99.basic.expr.StringExpr
import com.github.mmrsic.ti99.hw.TiBasicModule
import com.github.mmrsic.ti99.hw.TiBasicScreen

interface Statement : TiBasicExecutable

class PrintStatement(private val expressions: List<Any>) : Statement, Command {

    override val name: String = "PRINT"

    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        val maxCol = TiBasicScreen.MAX_COLUMNS - 2
        val currRow = 24
        var currCol = 3
        for (expression in expressions) {
            if (expression is NumericExpr) {
                expression.visitAllValues { nc ->
                    if (nc.isOverflow) {
                        machine.screen.print("")
                        machine.screen.print("* WARNING:")
                        machine.screen.print("  NUMBER TOO BIG" + if (programLineNumber != null) " IN $programLineNumber" else "")
                    }
                }
            }
            if (expression is Expression) {
                val characters = expression.displayValue()
                var lefOver = machine.screen.hchar(currRow, currCol, characters, maxCol)
                currCol += characters.length - lefOver.length
                while (lefOver.isNotEmpty()) {
                    machine.screen.scroll()
                    currCol = 3
                    val last = lefOver
                    lefOver = machine.screen.hchar(currRow, currCol, last, maxCol)
                    currCol += lefOver.length - last.length
                }
            } else {
                println("Ignored in print statement: $expression")
            }
        }
        machine.screen.scroll()
    }
}

class LetNumberStatement(val varName: String, val expr: NumericExpr) : Statement {
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) = machine.setNumericVariable(varName, expr)
}

class LetStringStatement(val varName: String, val expr: StringExpr) : Statement {
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) = machine.setStringVariable(varName, expr)
}

class EndStatement : Statement {
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        // TODO: End current program run
    }
}