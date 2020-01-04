package com.github.mmrsic.ti99.basic

import com.github.mmrsic.ti99.basic.expr.Expression
import com.github.mmrsic.ti99.basic.expr.NumericExpr
import com.github.mmrsic.ti99.basic.expr.StringExpr
import com.github.mmrsic.ti99.hw.TiBasicModule
import com.github.mmrsic.ti99.hw.TiBasicScreen

interface Statement : TiBasicExecutable

class PrintStatement(private val expressions: List<Any>) : Statement, Command {

    override val name: String = "PRINT"

    override fun execute(machine: TiBasicModule) {
        val maxCol = TiBasicScreen.MAX_COLUMNS - 2
        val currRow = 24
        var currCol = 3
        for (expression in expressions) {
            if (expression is Expression) {
                val characters = try {
                    expression.displayValue()
                } catch (e: NumberTooBig) {
                    machine.screen.print("")
                    machine.screen.print("* WARNING:")
                    machine.screen.print("  ${e.message}")
                    (if (e.sign < 0) "-" else " ") + "9.99999E+**"
                }
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

class AssignNumberStatement(val varName: String, val expr: NumericExpr) : Statement {
    override fun execute(machine: TiBasicModule) = machine.setNumericVariable(varName, expr)
}

class AssignStringStatement(val varName: String, val expr: StringExpr) : Statement {
    override fun execute(machine: TiBasicModule) {
        machine.setStringVariable(varName, expr)
    }
}

class EndStatement : Statement {
    override fun execute(machine: TiBasicModule) {
        // TODO: End current program run
    }
}