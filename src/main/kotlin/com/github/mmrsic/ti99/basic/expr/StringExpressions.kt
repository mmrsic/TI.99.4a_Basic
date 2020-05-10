package com.github.mmrsic.ti99.basic.expr

import com.github.mmrsic.ti99.hw.TiBasicModule
import com.github.mmrsic.ti99.hw.Variable

abstract class StringExpr : Expression {
   val maxStringSize = 255

   abstract override fun value(lambda: (value: Constant) -> Any): StringConstant
   override fun displayValue(): String {
      val resultCandidate = value().toNative()
      if (resultCandidate.length > maxStringSize) {
         return resultCandidate.substring(0, maxStringSize)
      }
      return resultCandidate
   }
}

data class StringConstant(override val constant: String) : StringExpr(), Constant {
   override fun value(lambda: (value: Constant) -> Any): StringConstant = this
   override fun toNative(): String = constant
   override fun listText(): String = "\"$constant\""

   companion object {
      val EMPTY = StringConstant("")
   }
}

data class StringVariable(override val name: String, val calc: (String) -> StringConstant) : StringExpr(), Variable {
   override fun value(lambda: (value: Constant) -> Any): StringConstant = calc.invoke(name)
   override fun listText(): String = name
}

data class StringArrayAccess(
   override val name: String, val arrayIndex: Expression, override val basicModule: TiBasicModule
) : StringExpr(), Variable, TiBasicModule.Dependent {

   override fun value(lambda: (value: Constant) -> Any): StringConstant {
      val result = basicModule.evaluateUserFunction(name, listOf(arrayIndex), null)
      if (result !is StringConstant) throw IllegalArgumentException("Non-string user-function: $name")
      return result
   }

   override fun listText() = "$name(${arrayIndex.listText()})"
}

data class StringConcatenation(val expressions: List<StringExpr>) : StringExpr() {
   override fun value(lambda: (value: Constant) -> Any): StringConstant =
      StringConstant(expressions.joinToString("") { expr -> expr.value().toNative() })

   override fun listText(): String = expressions.joinToString("&") { expr -> expr.listText() }
}