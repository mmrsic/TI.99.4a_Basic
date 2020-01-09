package com.github.mmrsic.ti99.basic.expr

abstract class StringFunction(val name: String) : StringExpr()

/**
 * The CHR$ function returns the character corresponding to the ASCII character code specified by numeric-expression.
 * The CHR$ function is the inverse of the [AscFunction]
 */
data class ChrFunction(private val numericExpr: NumericExpr) : StringFunction("CHR$") {
    override fun value() = StringConstant(toChar(numericExpr.value().toNative().toInt()))
}

/**
 * The SEG$ function returns a substring of a string-expression.
 * The string returned starts at position and extends for length characters. If position is beyond the end of
 * string-expression, an empty string is returned. If length extends beyond the end of string-expression, only the
 * characters to the end are returned.
 */
data class SegFunction(
    private val stringExpr: StringExpr,
    private val position: NumericExpr,
    private val length: NumericExpr
) : StringFunction("SEG$") {
    override fun value(): StringConstant {
        val original = stringExpr.value().toNative()
        val kotlinStart = position.value().toNative().toInt() - 1
        val kotlinEnd = kotlinStart + length.value().toNative().toInt()
        return StringConstant(original.substring(kotlinStart, kotlinEnd))
    }
}

/**
 * The STR$ function returns a string equivalent to numeric-expression.
 * This allows the functions, statements, and commands that act on strings to be used on the character representation
 * of numeric-expression. The STR$ is the inverse of the [ValFunction].
 */
data class StrFunction(private val numericExpr: NumericExpr) : StringFunction("STR$") {
    override fun value() = StringConstant(numericExpr.displayValue())
}


fun toChar(asciiCode: Int): String {
    return asciiCode.toChar().toString()
}