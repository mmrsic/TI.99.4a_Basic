package com.github.mmrsic.ti99.basic.betterparse

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.parser.Parser
import kotlin.math.pow

class ArithmeticsEvaluator : Grammar<Int>() {
    private val num by token("-?\\d+")
    private val lpar by token("\\(")
    private val rpar by token("\\)")
    private val mul by token("\\*")
    private val pow by token("\\^")

    val div by token("/")
    val minus by token("-")
    val plus by token("\\+")

    private val number by num use { text.toInt() }
    private val term: Parser<Int> by number or
            (skip(minus) and parser(this::term) map { -it }) or
            (skip(lpar) and parser(this::rootParser) and skip(rpar))

    private val powChain by leftAssociative(term, pow) { a, _, b -> a.toDouble().pow(b.toDouble()).toInt() }

    private val divMulChain by leftAssociative(powChain, div or mul use { type }) { a, op, b ->
        if (op == div) a / b else a * b
    }

    private val subSumChain by leftAssociative(divMulChain, plus or minus use { type }) { a, op, b ->
        if (op == plus) a + b else a - b
    }

    override val rootParser: Parser<Int> by subSumChain
}
