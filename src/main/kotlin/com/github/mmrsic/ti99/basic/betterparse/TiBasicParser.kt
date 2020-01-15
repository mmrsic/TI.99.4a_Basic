package com.github.mmrsic.ti99.basic.betterparse

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.parser.Parser
import com.github.mmrsic.ti99.basic.*
import com.github.mmrsic.ti99.basic.expr.*
import com.github.mmrsic.ti99.hw.TiBasicModule

/**
 * [Grammar] for TI Basic.
 */
class TiBasicParser(private val machine: TiBasicModule) : Grammar<TiBasicExecutable>() {
    /** Characters that may start a TI Basic variable. */
    private val nameStartChars = "A-Za-z@\\[\\]\\\\_"
    /** Characters a TI Basic variable may consist of. */
    private val nameChars = nameStartChars + "0-9"
    /** Suffix distinguishing a string variable from a numeric variable in TI Basic. */
    private val stringVarSuffix = "\\$"

    private val quoted by token("\".*\"")

    private val seg by token("SEG\\$")

    private val stringVarName by token("[$nameStartChars][$nameChars]*$stringVarSuffix")
    private val bye by token("\\bBYE\\b")
    private val end by token("\\bEND\\b")
    private val list by token("\\bLIST\\b")
    private val new by token("\\bNEW\\b")
    private val print by token("\\bPRINT\\b")
    private val run by token("\\bRUN\\b")

    private val minus by token("-")
    private val plus by token("\\+")
    private val asterisk by token("\\*")
    private val slash by token("/")
    private val exponentiation by token("\\^")
    private val arithmeticOperator by minus or plus or asterisk or slash or exponentiation
    private val ampersand by token("&")
    private val stringOperator by ampersand
    private val lessThanOrEqualTo by token("<=")
    private val notEquals by token("<>")
    private val lessThan by token("<")
    private val greaterThanOrEqualTo by token(">=")
    private val greaterThan by token(">")
    private val equals by token("=")
    private val openParenthesis by token("\\(")
    private val closeParenthesis by token("\\)")
    private val assign by equals
    private val comma by token(",")
    private val semicolon by token(";")
    private val colon by token(":")
    private val numberSign by token("#")
    private val printSeparator by colon or comma or semicolon use { text }
    private val e by token("\\B[Ee]")
    private val ws by token("\\s+", ignore = true)

    private val positiveInt by token("[0-9]+")
    private val fractionConst by token("\\.[0-9]+")
    private val numericConst by optional(minus) and positiveInt and optional(fractionConst) and
            optional(e and optional(minus or plus) and positiveInt) use {
        val factor = if (t1 == null) 1 else -1
        val mantissa = t2.text + (if (t3 != null) t3!!.text else "")
        val exponent = if (t4 != null) {
            val exponentValue = t4!!.t3.text
            "e" + (if (t4?.t2?.text == "-") "-" else "+") + exponentValue
        } else ""
        NumericConstant(factor * (mantissa + exponent).toDouble())
    }
    private val name by token("[$nameStartChars][$nameChars]*")

    private val stringConst by quoted use { StringConstant(text.drop(1).dropLast(1).replace("\"\"", "\"")) }
    private val segFun by skip(seg) and skip(openParenthesis) and
            parser(this::stringExpr) and skip(comma) and parser(this::numericExpr) and skip(comma) and parser(this::numericExpr) and
            skip(closeParenthesis) use { SegFunction(t1, t2, t3) }
    private val stringFun by segFun
    private val stringVarRef by stringVarName use {
        StringVariable(text) { varName -> machine.getStringVariableValue(varName) }
    }
    private val stringTerm: Parser<StringExpr> by stringConst or stringVarRef or stringFun or
            (skip(openParenthesis) and parser(this::stringExpr) and skip(closeParenthesis))

    private val stringExpr by leftAssociative(stringTerm, stringOperator) { a, _, b ->
        StringConcatenation(listOf(a, b))
    }

    private val numericVarRef by name use {
        NumericVariable(text) { varName -> machine.getNumericVariableValue(varName).value() }
    }
    private val term by numericConst or numericVarRef or
            (skip(minus) and parser(this::numericExpr) map { NegatedExpression(it) }) or
            (skip(openParenthesis) and parser(this::numericExpr) and skip(closeParenthesis))
    private val expChain: Parser<NumericExpr> by leftAssociative(term, exponentiation) { a, _, b ->
        Exponentiation(a, b)
    }
    private val mulDivChain by leftAssociative(expChain, asterisk or slash use { type }) { a, op, b ->
        if (op == asterisk) Multiplication(a, b) else Division(a, b)
    }
    private val plusMinusChain by leftAssociative(mulDivChain, plus or minus use { type }) { a, op, b ->
        if (op == plus) Addition(a, b) else Subtraction(a, b)
    }
    private val relationalOperator = equals or notEquals or lessThanOrEqualTo or lessThan or greaterThanOrEqualTo or
            greaterThan use { RelationalExpr.Operator.fromSymbol(text) }

    private val numericExpr by leftAssociative(plusMinusChain, relationalOperator) { a, op, b ->
        RelationalNumericExpr(a, op, b)
    } or ((stringExpr and relationalOperator and stringExpr) use {
        RelationalStringExpr(t1, t2, t3)
    })

    private val expr by numericExpr or stringExpr

    private val newCmd = new and optional(ws and optional(numericExpr or name)) asJust NewCommand()
    private val runCmd by skip(run) and optional(positiveInt) map { RunCommand(it?.text?.toInt()) }
    private val byeCmd by bye asJust ByeCommand()
    private val listRangeCmd by skip(list) and positiveInt and skip(minus) and positiveInt use {
        ListCommand(t1.text.toInt(), t2.text.toInt())
    }
    private val listFromCmd by skip(list) and positiveInt and skip(minus) use { ListCommand(text.toInt(), null) }
    private val listToCmd by skip(list) and skip(minus) and positiveInt use { ListCommand(null, text.toInt()) }
    private val listLineCmd by skip(list) and positiveInt use { ListCommand(text.toInt()) }
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

    private val assignNumberStmt by numericVarRef and skip(assign) and (numericExpr) use {
        LetNumberStatement(t1.name, t2)
    }
    private val assignStringStmt by stringVarRef and skip(assign) and stringExpr use {
        LetStringStatement(t1.name, t2)
    }
    private val endStmt by end asJust EndStatement()

    private val cmdParser = newCmd or runCmd or byeCmd or
            listRangeCmd or listToCmd or listFromCmd or listLineCmd or listCmd
    private val stmtParser = printStmt or assignNumberStmt or assignStringStmt or endStmt

    private val programLineParser by positiveInt and stmtParser use {
        StoreProgramLineCommand(ProgramLine(t1.text.toInt(), listOf(t2)))
    }

    override val rootParser by cmdParser or stmtParser or programLineParser
}

