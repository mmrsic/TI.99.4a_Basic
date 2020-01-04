package com.github.mmrsic.ti99.basic.betterparse

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.mmrsic.ti99.basic.*
import com.github.mmrsic.ti99.basic.expr.NumericConstant
import com.github.mmrsic.ti99.basic.expr.NumericVariable
import com.github.mmrsic.ti99.basic.expr.StringConstant
import com.github.mmrsic.ti99.hw.TiBasicModule

class TiBasicParser(private val machine: TiBasicModule) : Grammar<TiBasicExecutable>() {

    private val quote by token("\"")
    private val assign by token("=")
    private val minus by token("-")
    private val comma by token(",")
    private val semicolon by token(";")
    private val colon by token(":")
    private val printSeparator by colon or comma or semicolon

    private val new by token("\\s*NEW.*")
    private val run by token("RUN")
    private val bye by token("BYE")
    private val list by token("LIST")
    private val print by token("PRINT")
    private val end by token("END")

    private val ws by token("\\s+", ignore = true) // Token is used even if not referenced!

    private val positiveInt by token("[0-9]+")
    private val fractionConst by token("\\.[0-9]+")
    private val name by token("[A-Z]+")

    private val numericConst by optional(minus) and positiveInt and optional(fractionConst) use {
        val factor = if (t1==null) 1 else -1
        NumericConstant(factor * (t2.text + (if (t3 != null) t3!!.text else "")).toDouble())
    }
    private val numericVarRef by name use {
        NumericVariable(text) { varName -> machine.getNumericVariableValue(varName).calculate() }
    }
    private val numericExpr by numericConst or numericVarRef
    private val stringConst by skip(quote) and token(".*") and skip(quote) use { StringConstant(text) }

    private val expr by numericExpr or stringConst

    private val newCmd = new asJust NewCommand()
    private val runCmd by skip(run) and optional(positiveInt) map { RunCommand(it?.text?.toInt()) }
    private val byeCmd by bye asJust ByeCommand()
    private val listRangeCmd by skip(list) and positiveInt and skip(minus) and positiveInt use {
        ListCommand(t1.text.toInt(), t2.text.toInt())
    }
    private val listFromCmd by skip(list) and positiveInt and skip(minus) use {
        ListCommand(text.toInt(), null)
    }
    private val listToCmd by skip(list) and skip(minus) and positiveInt use {
        ListCommand(null, text.toInt())
    }
    private val listLineCmd by skip(list) and positiveInt use {
        ListCommand(text.toInt())
    }
    private val listCmd by list asJust ListCommand(null)

    private val printStmt by skip(print) and
            zeroOrMore(printSeparator) and
            optional(expr) and
            zeroOrMore(oneOrMore(printSeparator) and expr) and
            zeroOrMore(printSeparator) use {
        val printArgs = mutableListOf<Any>()
        // Add all leading separators
        printArgs.addAll(t1)
        // Add first expression if present
        if (t2 != null) printArgs.add(t2!!)
        // Add second expression with leading separators if present
        for (separatorsBeforeExpr in t3) {
            printArgs.addAll(separatorsBeforeExpr.t1)
            printArgs.add(separatorsBeforeExpr.t2)
        }
        // Add all trailing sepearators
        printArgs.addAll(t4)
        PrintStatement(printArgs)
    }

    private val assignNumberStmt by numericVarRef and skip(assign) and numericConst use {
        AssignNumberStatement(t1.name, t2)
    }
    private val endStmt by end asJust EndStatement()

    private val cmdParser = newCmd or runCmd or byeCmd or
            listRangeCmd or listToCmd or listFromCmd or listLineCmd or listCmd
    private val stmtParser = printStmt or assignNumberStmt or endStmt

    private val programLineParser by positiveInt and stmtParser use {
        StoreProgramLineCommand(ProgramLine(t1.text.toInt(), listOf(t2)))
    }


    override val rootParser by cmdParser or stmtParser or programLineParser
}

