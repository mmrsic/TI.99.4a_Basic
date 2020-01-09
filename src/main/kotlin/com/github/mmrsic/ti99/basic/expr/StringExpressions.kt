package com.github.mmrsic.ti99.basic.expr

abstract class StringExpr : Expression {
    val maxStringSize = 255

    abstract override fun value(): StringConstant
    override fun displayValue(): String {
        val resultCandidate = value().toNative()
        if (resultCandidate.length > maxStringSize) {
            return resultCandidate.substring(0, maxStringSize)
        }
        return resultCandidate
    }
}

data class StringConstant(val constant: String) : StringExpr(), Constant {
    override fun value(): StringConstant = this
    override fun toNative(): String = constant
}

data class StringVariable(val name: String, val calc: (String) -> StringConstant) : StringExpr() {
    override fun value() = calc.invoke(name)
}

data class StringConcatenation(val expressions: List<StringExpr>) : StringExpr() {
    override fun value(): StringConstant =
        StringConstant(expressions.joinToString("") { expr -> expr.value().toNative() })
}