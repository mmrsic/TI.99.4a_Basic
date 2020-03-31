package com.github.mmrsic.ti99.basic.betterparse

import com.github.h0tk3y.betterParse.combinators.and
import com.github.h0tk3y.betterParse.combinators.asJust
import com.github.h0tk3y.betterParse.combinators.leftAssociative
import com.github.h0tk3y.betterParse.combinators.map
import com.github.h0tk3y.betterParse.combinators.oneOrMore
import com.github.h0tk3y.betterParse.combinators.optional
import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.combinators.separated
import com.github.h0tk3y.betterParse.combinators.separatedTerms
import com.github.h0tk3y.betterParse.combinators.skip
import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.combinators.zeroOrMore
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

    private val quoted by token(""""([^"]|"")*"""")

    private val chr by token("CHR\\$")
    private val seg by token("SEG\\$")
    private val str by token("STR\\$")
    private val stringVarName by token("[$nameStartChars][$nameChars]*$stringVarSuffix")

    private val abs by token("\\bABS\\b")
    private val atn by token("\\bATN\\b")
    private val asc by token("\\bASC\\b")
    private val breakToken by token("BREAK")
    private val bye by token("\\bBYE\\b")
    private val cos by token("\\bCOS\\b")
    private val joyst by token("CALL\\s+JOYST\\b")
    private val key by token("CALL\\s+KEY\\b")
    private val call by token("CALL")
    private val char by token("CHAR")
    private val clear by token("CLEAR")
    private val color by token("COLOR")
    private val continueToken by token("""CON(TINUE)?""")
    private val data by token("DATA")
    private val def by token("\\bDEF\\b")
    private val display by token("DISPLAY")
    private val elseToken by token("ELSE")
    private val end by token("\\bEND\\b")
    private val exp by token("\\bEXP\\b")
    private val forToken by token("FOR")
    private val gchar by token("GCHAR\\b")
    private val gosub by token("GOSUB")
    private val goto by token("""GO\s?TO""")
    private val hchar by token("HCHAR")
    private val ifToken by token("IF")
    private val input by token("INPUT")
    private val int by token("INT")
    private val len by token("\\bLEN\\b")
    private val let by token("LET")
    private val list by token("\\bLIST\\b")
    private val log by token("\\bLOG\\b")
    private val new by token("\\bNEW\\b")
    private val next by token("NEXT")
    private val number by token("""NUM(BER)?""")
    private val on by token("ON")
    private val pos by token("\\bPOS\\b")
    private val print by token("\\bPRINT\\b")
    private val randomize by token("\\bRANDOMIZE\\b")
    private val read by token("READ")
    private val remark by token("""REM(ARK)?.*""")
    private val restore by token("RESTORE")
    private val resequence by token("""RES(EQUENCE)?""")
    private val returnToken by token("RETURN")
    private val rnd by token("\\bRND\\b")
    private val run by token("\\bRUN\\b")
    private val screen by token("SCREEN")
    private val sgn by token("\\bSGN\\b")
    private val sin by token("\\bSIN\\b")
    private val sound by token("SOUND")
    private val sqr by token("\\bSQR\\b")
    private val step by token("STEP")
    private val stop by token("STOP")
    private val tab by token("TAB")
    private val tan by token("\\bTAN\\b")
    private val then by token("THEN")
    private val to by token("TO\\b")
    private val trace by token("TRACE")
    private val unbreak by token("UNBREAK")
    private val untrace by token("UNTRACE")
    private val valToken by token("\\bVAL\\b")
    private val vchar by token("VCHAR")

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

    private val positiveDecimal by token("[0-9]*\\.[0-9]+")
    private val positiveInt by token("[0-9]+")
    private val numericConst: Parser<NumericConstant> by optional(minus or plus) and (positiveDecimal or positiveInt) and
            optional(e and optional(minus or plus) and positiveInt) use {
        val factor = if (t1?.text == minus.pattern) -1 else 1
        val mantissa = t2.text
        val exponent = if (t3 != null) {
            val exponentValue = t3!!.t3.text
            "e" + (if (t3?.t2?.text == "-") "-" else "+") + exponentValue
        } else ""
        NumericConstant(factor * (mantissa + exponent).toDouble())
    }
    private val name by token("[$nameStartChars][$nameChars]*")

    // PARSERS //

    private val singleNumericArg by skip(openParenthesis) and parser(::numericExpr) and skip(closeParenthesis)
    private val singleStringArg by skip(openParenthesis) and parser(::stringExpr) and skip(closeParenthesis)
    private val positiveIntConst by positiveInt use { text.toInt() }

    private val stringConst by quoted use { StringConstant(text.drop(1).dropLast(1).replace("\"\"", "\"")) }
    private val chrFun by skip(chr) and singleNumericArg use { ChrFunction(this) }
    private val segFun by skip(seg) and skip(openParenthesis) and
            parser(::stringExpr) and skip(comma) and parser(::numericExpr) and skip(comma) and parser(::numericExpr) and
            skip(closeParenthesis) use { SegFunction(t1, t2, t3) }
    private val strFun by skip(str) and singleNumericArg use { StrFunction(this) }
    private val stringFun by chrFun or segFun or strFun
    private val stringVarRef by stringVarName use {
        StringVariable(text) { varName -> machine.getStringVariableValue(varName) }
    }
    private val stringTerm: Parser<StringExpr> by stringConst or stringVarRef or stringFun or
            (skip(openParenthesis) and parser(::stringExpr) and skip(closeParenthesis))

    private val stringExpr by leftAssociative(stringTerm, stringOperator) { a, _, b ->
        StringConcatenation(listOf(a, b))
    }

    private val absFun by skip(abs) and singleNumericArg use { AbsFunction(this) }
    private val ascFun by skip(asc) and singleStringArg use { AscFunction(this) }
    private val atnFun by skip(atn) and singleNumericArg use { AtnFunction(this) }
    private val cosFun by skip(cos) and singleNumericArg use { CosFunction(this) }
    private val expFun by skip(exp) and singleNumericArg use { ExpFunction(this) }
    private val intFun by skip(int) and singleNumericArg use { IntFunction(this) }
    private val lenFun by skip(len) and singleStringArg use { LenFunction(this) }
    private val logFun by skip(log) and singleNumericArg use { LogFunction(this) }
    private val posFun by skip(pos) and skip(openParenthesis) and stringExpr and skip(comma) and stringExpr and
            skip(comma) and parser(::numericExpr) and skip(closeParenthesis) use { PosFunction(t1, t2, t3) }
    private val rndFun by rnd asJust RndFunction(machine::nextRandom)
    private val sgnFun by skip(sgn) and singleNumericArg use { SgnFunction(this) }
    private val sinFun by skip(sin) and singleNumericArg use { SinFunction(this) }
    private val sqrFun by skip(sqr) and singleNumericArg use { SqrFunction(this) }
    private val tabFun by skip(tab) and singleNumericArg use { TabFunction(this) }
    private val tanFun by skip(tan) and singleNumericArg use { TanFunction(this) }
    private val valFun by skip(valToken) and singleStringArg use { ValFunction(this) }
    private val numericFun by absFun or ascFun or atnFun or cosFun or expFun or intFun or lenFun or logFun or posFun or
            rndFun or sgnFun or sinFun or sqrFun or tanFun or valFun
    private val numericArrRef by name and singleNumericArg use { NumericArrayAccess(t1.text, t2, machine) }
    private val numericVarRef by name use {
        NumericVariable(text) { varName -> machine.getNumericVariableValue(varName).value() }
    }
    private val term by numericConst or numericArrRef or numericVarRef or numericFun or
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

    private val printToken by expr or tabFun or stringFun or numericFun
    private val printStmt by skip(print or display) and zeroOrMore(printSeparator) and optional(printToken) and
            zeroOrMore(oneOrMore(printSeparator) and printToken) and zeroOrMore(printSeparator) use {
        val printArgs = mutableListOf<Expression>()
        // Add all leading separators
        printArgs.addAll(t1.map { PrintSeparator.fromString(it)!! })
        // Add first expression if present
        if (t2 != null) printArgs.add(t2!!)
        // Add second expression with leading separators if present
        for (separatorsBeforeExpr in t3) {
            printArgs.addAll(separatorsBeforeExpr.t1.map { PrintSeparator.fromString(it)!! })
            printArgs.add(separatorsBeforeExpr.t2)
        }
        // Add all trailing separators
        printArgs.addAll(t4.map { PrintSeparator.fromString(it)!! })
        PrintStatement(printArgs)
    }
    private val assignNumberStmt by skip(optional(let)) and numericVarRef and skip(assign) and (numericExpr) use {
        LetNumberStatement(t1.name, t2)
    }
    private val assignStringStmt by skip(optional(let)) and stringVarRef and skip(assign) and stringExpr use {
        LetStringStatement(t1.name, t2)
    }
    private val defNumericFunStmt by skip(def) and numericVarRef and optional(singleNumericArg or singleStringArg) and
            skip(equals) and numericExpr use {
        DefineFunctionStatement(t1.name, t2?.listText(), t3)
    }
    private val defStringFunStmt by skip(def) and stringVarRef and optional(singleNumericArg or singleStringArg) and
            skip(equals) and stringExpr use {
        DefineFunctionStatement(t1.name, t2?.listText(), t3)
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
    private val dataContent = (numericConst as Parser<Constant> or stringConst or emptyDataString or dataString)
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
    private val randomizeStmt by skip(randomize) and optional(numericExpr) use { RandomizeStatement(this) }

    // CALL SUBPROGRAM PARSERS

    private val callChar: Parser<Statement> by skip(call and char and openParenthesis) and
            numericExpr and skip(comma) and stringExpr and skip(closeParenthesis) use { CharSubprogram(t1, t2) }
    private val callClear: Parser<Statement> by skip(call) and clear asJust ClearSubprogram()
    private val callColor: Parser<Statement> by skip(call) and skip(color) and skip(openParenthesis) and numericExpr and
            skip(comma) and numericExpr and skip(comma) and numericExpr and skip(closeParenthesis) use {
        ColorSubprogram(t1, t2, t3)
    }
    private val callGchar: Parser<Statement> by skip(call and gchar and openParenthesis) and
            numericExpr and skip(comma) and numericExpr and skip(comma) and numericVarRef and skip(closeParenthesis) use {
        GcharSubprogram(t1, t2, t3)
    }
    private val callHchar: Parser<Statement> by skip(call and hchar and openParenthesis) and
            numericExpr and skip(comma) and numericExpr and skip(comma) and numericExpr and
            optional(skip(comma) and numericExpr) and skip(closeParenthesis) use {
        val repetition = t4
        if (repetition != null) HcharSubprogram(t1, t2, t3, repetition) else HcharSubprogram(t1, t2, t3)
    }
    private val callJoyst: Parser<Statement> by skip(joyst and openParenthesis) and numericExpr and skip(comma) and
            numericVarRef and skip(comma) and numericVarRef and skip(closeParenthesis) use {
        JoystSubprogram(t1, t2, t3)
    }
    private val callKey: Parser<Statement> by skip(key and openParenthesis) and numericExpr and
            skip(comma) and numericVarRef and skip(comma) and numericVarRef and skip(closeParenthesis) use {
        KeySubprogram(t1, t2, t3)
    }
    private val callScreen: Parser<Statement> by skip(call and screen and openParenthesis) and
            numericExpr and skip(closeParenthesis) use { ScreenSubprogram(this) }
    private val callSound: Parser<Statement> by skip(call and sound and openParenthesis) and
            numericExpr and skip(comma) and numericExpr and skip(comma) and numericExpr and
            optional(
                skip(comma) and numericExpr and skip(comma) and numericExpr and
                        optional(
                            skip(comma) and numericExpr and skip(comma) and numericExpr and
                                    optional(skip(comma) and numericExpr and skip(comma) and numericExpr)
                        )
            ) and skip(closeParenthesis) use {
        when {
            t4 == null -> SoundSubprogram(t1, t2, t3)
            t4?.t3 == null -> SoundSubprogram(t1, t2, t3, t4?.t1, t4?.t2)
            t4?.t3?.t3 == null -> SoundSubprogram(t1, t2, t3, t4?.t1, t4?.t2, t4?.t3?.t1, t4?.t3?.t2)
            else -> SoundSubprogram(t1, t2, t3, t4?.t1, t4?.t2, t4?.t3?.t1, t4?.t3?.t2, t4?.t3?.t3?.t1, t4?.t3?.t3?.t2)
        }
    }
    private val callVchar: Parser<Statement> by skip(call and vchar and openParenthesis) and
            numericExpr and skip(comma) and numericExpr and skip(comma) and numericExpr and
            optional(skip(comma) and numericExpr) and skip(closeParenthesis) use {
        val repetition = t4
        if (repetition != null) VcharSubprogram(t1, t2, t3, repetition) else VcharSubprogram(t1, t2, t3)
    }
    private val callParser: Parser<Statement> by callChar or callClear or callColor or callGchar or callHchar or
            callJoyst or callKey or callScreen or callSound or callVchar

    // PARSER HIERARCHY

    private val cmdParser by newCmd or runCmd or byeCmd or numberCmd or resequenceCmd or
            breakCmd or continueCmd or unbreakCmd or traceCmd or untraceCmd or
            listRangeCmd or listToCmd or listFromCmd or listLineCmd or listCmd
    private val stmtParser by printStmt or assignNumberStmt or assignStringStmt or endStmt or remarkStmt or
            callParser or breakStmt or unbreakStmt or traceCmd or forToStepStmt or nextStmt or stopStmt or ifStmt or
            inputStmt or gotoStmt or onGotoStmt or gosubStmt or returnStmt or dataStmt or readStmt or restoreStmt or
            randomizeStmt or defNumericFunStmt or defStringFunStmt

    private val programLineParser by positiveIntConst and stmtParser use {
        StoreProgramLineCommand(ProgramLine(t1, listOf(t2)))
    }
    private val removeProgramLineParser by positiveInt use { RemoveProgramLineCommand(Integer.parseInt(text)) }

    override val rootParser by cmdParser or stmtParser or programLineParser or removeProgramLineParser
}

