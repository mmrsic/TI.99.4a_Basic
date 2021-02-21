package com.github.mmrsic.ti99.basic.betterparse

import com.github.h0tk3y.betterParse.combinators.optional
import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.combinators.separatedTerms
import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.mmrsic.ti99.basic.expr.Constant
import com.github.mmrsic.ti99.basic.expr.StringConstant

/** A [Grammar] for TI Basic's DATA elements. */
class DataParser : Grammar<List<Constant>>() {

   private val quotedTok by token(""""([^"]|"")*"""")
   private val commaTok by token(",")
   private val ws by token("\\s+", ignore = true)
   private val unquotedTok by token("[^,]+")

   private val quotedString by quotedTok use { StringConstant(unquote(text)) }
   private val unquotedString by optional(unquotedTok) use { if (this != null) StringConstant(text.trim()) else StringConstant.EMPTY }

   override val rootParser by separatedTerms(quotedString or unquotedString, commaTok, acceptZero = true)

   // HELPERS //

   private fun unquote(quoted: String): String = quoted.drop(1).dropLast(1).replace("\"\"", "\"")
}