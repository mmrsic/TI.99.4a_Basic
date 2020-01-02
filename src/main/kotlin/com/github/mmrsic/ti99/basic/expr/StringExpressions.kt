package com.github.mmrsic.ti99.basic.expr

abstract class StringExpr : Expression() {
    abstract override fun calculate(): String
    override fun displayValue() = calculate()
}

data class StringConstant(val constant: String) : StringExpr() {
    override fun calculate(): String = constant
}