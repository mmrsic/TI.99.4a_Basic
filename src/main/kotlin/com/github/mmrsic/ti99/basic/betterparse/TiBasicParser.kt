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

    // TOKENS //

    private val quoted by token("\".*\"")

    private val seg by token("SEG\\$")
    private val stringVarName by token("[$nameStartChars][$nameChars]*$stringVarSuffix")

    private val breakToken by token("BREAK")
    private val bye by token("\\bBYE\\b")
    private val call by token("CALL")
    private val char by token("CHAR")
    private val clear by token("CLEAR")
    private val continueToken by token("""CON(TINUE)?""")
    private val data by token("DATA")
    private val elseToken by token("ELSE")
    private val end by token("\\bEND\\b")
    private val forToken by token("FOR")
    private val gosub by token("GOSUB")
    private val goto by token("""GO\s?TO""")
    private val hchar by token("HCHAR")
    private val ifToken by token("IF")
    private val input by token("INPUT")
    private val int by token("INT")
    private val let by token("LET")
    private val list by token("\\bLIST\\b")
    private val new by token("\\bNEW\\b")
    private val next by token("NEXT")
    private val number by token("""NUM(BER)?""")
    private val on by token("ON")
    private val print by token("\\bPRINT\\b")
    private val read by token("READ")
    private val remark by token("""REM(ARK)?.*""")
    private val restore by token("RESTORE")
    private val resequence by token("""RES(EQUENCE)?""")
    private val returnToken by token("RETURN")
    private val run by token("\\bRUN\\b")
    private val step by token("STEP")
    private val stop by token("STOP")
    private val then by token("THEN")
    private val to by token("TO")
    private val trace by token("TRACE")
    private val unbreak by token("UNBREAK")
    private val untrace by token("UNTRACE")

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
    private val doubleComma by token(",,", ignore = true)
    private val comma by token(",")
    private val semicolon by token(";")
    private val colon by token(":")
    private val numberSign by token("#")
    private val printSeparator by colon or comma or semicolon use { text }
    private val e by token("\\B[Ee]")
    private val ws by token("\\s+", ignore = true)

    private val positiveInt by token("[0-9]+")
    private val fractionPart by token("\\.[0-9]+")
    private val fractionConst by fractionPart use { NumericConstant(text.toDouble()) }
    private val numericConst: Parser<NumericConstant> by optional(minus) and positiveInt and optional(fractionPart) and
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

    // PARSERS //

    private val positiveIntConst by positiveInt use { text.toInt() }

    private val stringConst by quoted use { StringConstant(text.drop(1).dropLast(1).replace("\"\"", "\"")) }
    private val segFun by skip(seg) and skip(openParenthesis) and
            parser(::stringExpr) and skip(comma) and parser(::numericExpr) and skip(comma) and parser(::numericExpr) and
            skip(closeParenthesis) use { SegFunction(t1, t2, t3) }
    private val stringFun by segFun
    private val stringVarRef by stringVarName use {
        StringVariable(text) { varName -> machine.getStringVariableValue(varName) }
    }
    private val stringTerm: Parser<StringExpr> by stringConst or stringVarRef or stringFun or
            (skip(openParenthesis) and parser(::stringExpr) and skip(closeParenthesis))

    private val stringExpr by leftAssociative(stringTerm, stringOperator) { a, _, b ->
        StringConcatenation(listOf(a, b))
    }

    private val intFun by skip(int) and skip(openParenthesis) and parser(::numericExpr) and skip(closeParenthesis) use {
        IntFunction(this)
    }
    private val numericFun by intFun
    private val numericArrRef by name and skip(openParenthesis) and parser(::numericExpr) and skip(closeParenthesis) use {
        NumericArrayAccess(t1.text, t2, machine)
    }
    private val numericVarRef by name use {
        NumericVariable(text) { varName -> machine.getNumericVariableValue(varName).value() }
    }
    private val term by numericConst or numericArrRef or numericVarRef or numericFun or fractionConst or
            (skip(minus) and parser(::numericExpr) map { NegatedExpression(it) }) or
            (skip(openParenthesis) and parser(::numericExpr) and skip(closeParenthesis))
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

    // COMMAND PARSERS

    private val newCmd = new and optional(ws and optional(numericExpr or name)) asJust NewCommand()
    private val runCmd by skip(run) and optional(positiveIntConst) map { RunCommand(it) }
    private val byeCmd by bye asJust ByeCommand()
    private val listRangeCmd by skip(list) and positiveIntConst and skip(minus) and positiveIntConst use {
        ListCommand(t1, t2)
    }
    private val listFromCmd by skip(list) and positiveIntConst and skip(minus) use { ListCommand(this, null) }
    private val listToCmd by skip(list) and skip(minus) and positiveIntConst use { ListCommand(null, this) }
    private val listLineCmd by skip(list) and positiveIntConst use { ListCommand(this) }
    private val listCmd by list asJust ListCommand(null)
    private val numberCmd by skip(number) and optional(positiveIntConst) and optional(skip(comma) and positiveIntConst) use {
        when {
            t1 == null && t2 == null -> NumberCommand()
            t1 != null && t2 == null -> NumberCommand(initialLine = t1!!)
            t1 == null && t2 != null -> NumberCommand(increment = t2!!)
            t1 != null && t2 != null -> NumberCommand(initialLine = t1!!, increment = t2!!)
            else -> throw IllegalStateException("Missing branch implementation for combination: t1=$t1 / t2=$t2")
        }
    }
    private val resequenceCmd by skip(resequence) and optional(positiveIntConst) and optional(skip(comma) and positiveIntConst) use {
        when {
            t1 == null && t2 == null -> ResequenceCommand()
            t1 != null && t2 == null -> ResequenceCommand(initialLine = t1!!)
            t1 == null && t2 != null -> ResequenceCommand(increment = t2!!)
            t1 != null && t2 != null -> ResequenceCommand(initialLine = t1!!, increment = t2!!)
            else -> throw IllegalStateException("Missing branch implementation for combination: t1=$t1 / t2=$t2")
        }
    }
    private val breakCmd by skip(breakToken) and separatedTerms(positiveIntConst, comma, acceptZero = false) use {
        BreakCommand(this)
    }
    private val continueCmd by continueToken asJust ContinueCommand()
    private val unbreakCmd by skip(unbreak) and optional(separatedTerms(positiveIntConst, comma)) use {
        if (this == null) UnbreakCommand() else UnbreakCommand(this)
    }
    private val traceCmd by trace asJust TraceCommand()
    private val untraceCmd by untrace asJust UntraceCommand()

    // STATEMENT PARSERS

    private val printStmt by skip(print) and
            zeroOrMore(printSeparator) and
            optional(expr) and
            zeroOrMore(oneOrMore(printSeparator) and expr) and
            zeroOrMore(printSeparator) use {
        val printArgs = mutableListOf<Expression>()
        // Add all leading separators
        printArgs.addAll(t1.map { PrintToken.fromString(it)!! })
        // Add first expression if present
        if (t2 != null) printArgs.add(t2!!)
        // Add second expression with leading separators if present
        for (separatorsBeforeExpr in t3) {
            printArgs.addAll(separatorsBeforeExpr.t1.map { PrintToken.fromString(it)!! })
            printArgs.add(separatorsBeforeExpr.t2)
        }
        // Add all trailing separators
        printArgs.addAll(t4.map { PrintToken.fromString(it)!! })
        PrintStatement(printArgs)
    }
    private val assignNumberStmt by skip(optional(let)) and numericVarRef and skip(assign) and (numericExpr) use {
        LetNumberStatement(t1.name, t2)
    }
    private val assignStringStmt by skip(optional(let)) and stringVarRef and skip(assign) and stringExpr use {
        LetStringStatement(t1.name, t2)
    }
    private val endStmt by end asJust EndStatement()
    private val stopStmt by stop asJust StopStatement()
    private val forToStepStmt by skip(forToken) and assignNumberStmt and skip(to) and numericExpr and
            optional(skip(step) and numericExpr) use {
        ForToStepStatement(t1, t2, t3)
    }
    private val nextStmt by skip(next) and numericVarRef use { NextStatement(name) }
    private val remarkStmt by remark use {
        when {
            text.startsWith("REMARK") -> RemarkStatement(text.substringAfter("REMARK"))
            text.startsWith("REM") -> RemarkStatement(text.substringAfter("REM"))
            else -> throw IllegalArgumentException("Illegal REMARK: $text")
        }
    }
    private val gotoStmt by skip(goto) and (positiveIntConst) map { lineNum -> GoToStatement(lineNum) }
    private val onGotoStmt by skip(on) and numericExpr and skip(goto) and separatedTerms(positiveIntConst, comma) use {
        OnGotoStatement(t1, t2)
    }
    private val gosubStmt by skip(gosub) and positiveIntConst map { programLine -> GosubStatement(programLine) }
    private val returnStmt by returnToken asJust ReturnStatement()
    private val breakStmt by skip(breakToken) and separated(positiveIntConst, comma, true) use { BreakStatement(terms) }
    private val unbreakStmt by skip(unbreak) and separated(positiveIntConst, comma, true) use {
        UnbreakStatement(terms)
    }
    private val ifStmt by skip(ifToken) and numericExpr and skip(then) and positiveIntConst and
            optional(skip(elseToken) and positiveIntConst) use {
        IfStatement(t1, t2, t3)
    }
    private val varRef = numericArrRef or numericVarRef or stringVarRef
    private val inputStmt by skip(input) and optional(stringExpr and skip(colon)) and
            separatedTerms(varRef, comma) use {
        val prompt: StringExpr? = t1
        val varNameList: List<Expression> = t2
        InputStatement(prompt, varNameList)
    }
    private val emptyDataString: Parser<Constant> by doubleComma asJust StringConstant.EMPTY
    private val dataString: Parser<Constant> = name use { StringConstant(this.text) }
    private val dataContent: Parser<Constant> =
        (numericConst or stringConst or emptyDataString or dataString) as Parser<Constant>
    private val dataStmt by skip(data) and separated(dataContent, doubleComma or comma, true) use {
        val data = mutableListOf<Constant>()
        for ((index, term) in terms.withIndex()) {
            data.add(term)
            val nextIndex = index + 1
            if (nextIndex < terms.size && separators[index].text == ",,") data.add(StringConstant.EMPTY)
        }
        DataStatement(data)
    }
    private val readStmt by skip(read) and separatedTerms(varRef, comma, false) use { ReadStatement(this) }
    private val restoreStmt by skip(restore) and optional(positiveIntConst) use { RestoreStatement(this) }

    // CALL SUBPROGRAM PARSERS

    private val callChar: Parser<Statement> by skip(call and char and openParenthesis) and
            numericExpr and skip(comma) and stringConst and skip(closeParenthesis) use { CharSubprogram(t1, t2) }
    private val callClear: Parser<Statement> by skip(call) and clear asJust ClearSubprogram()
    private val callHchar: Parser<Statement> by skip(call and hchar and openParenthesis) and
            numericExpr and skip(comma) and numericExpr and skip(comma) and numericExpr and
            optional(skip(comma) and numericExpr) and skip(closeParenthesis) use {
        val repetition = t4
        if (repetition != null) HcharSubprogram(t1, t2, t3, repetition) else HcharSubprogram(t1, t2, t3)
    }
    private val callParser: Parser<Statement> by callChar or callClear or callHchar

    // PARSER HIERARCHY

    private val cmdParser by newCmd or runCmd or byeCmd or numberCmd or resequenceCmd or
            breakCmd or continueCmd or unbreakCmd or traceCmd or untraceCmd or
            listRangeCmd or listToCmd or listFromCmd or listLineCmd or listCmd
    private val stmtParser by printStmt or assignNumberStmt or assignStringStmt or endStmt or remarkStmt or
            callParser or breakStmt or unbreakStmt or traceCmd or forToStepStmt or nextStmt or stopStmt or ifStmt or
            inputStmt or gotoStmt or onGotoStmt or gosubStmt or returnStmt or dataStmt or readStmt or restoreStmt

    private val programLineParser by positiveIntConst and stmtParser use {
        StoreProgramLineCommand(ProgramLine(t1, listOf(t2)))
    }
    private val removeProgramLineParser by positiveInt use { RemoveProgramLineCommand(Integer.parseInt(text)) }

    override val rootParser by cmdParser or stmtParser or programLineParser or removeProgramLineParser
}

