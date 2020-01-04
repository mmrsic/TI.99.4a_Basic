package com.github.mmrsic.ti99.basic.expr

abstract class StringExpr : Expression() {
    abstract override fun calculate(): String
    override fun displayValue() = calculate()
}

data class StringConstant(val constant: String) : StringExpr() {
    override fun calculate(): String = constant
}

data class StringVariable(val name: String, val calc: (String) -> String) : StringExpr() {
    override fun calculate(): String = calc.invoke(name)
}

data class StringConcatenation(val expressions: List<StringExpr>) : StringExpr() {
    override fun calculate(): String = expressions.joinToString("") { expr -> expr.calculate() }
}