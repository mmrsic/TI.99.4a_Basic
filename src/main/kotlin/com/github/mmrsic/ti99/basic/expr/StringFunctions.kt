package com.github.mmrsic.ti99.basic.expr

abstract class StringFunction(val name: String) : StringExpr() {
    override fun listText(): String {
        return "$name(${listArgs()})"
    }

    /** Arguments of this function as provided by the LIST statement. */
    abstract fun listArgs(): String
}

/**
 * The CHR$ function returns the character corresponding to the ASCII character code specified by numeric-expression.
 * The CHR$ function is the inverse of the [AscFunction]
 */
data class ChrFunction(private val numericExpr: NumericExpr) : StringFunction("CHR$") {
    override fun value() = StringConstant(toChar(numericExpr.value().toNative().toInt()))
    override fun listArgs(): String = numericExpr.listText()
}

/**
 * The SEG$ function returns a substring of a string-expression.
 * The string returned starts at position and extends for length characters. If position is beyond the end of
 * string-expression, an empty string is returned. If length extends beyond the end of string-expression, only the
 * characters to the end are returned.
 */
data class SegFunction(private val str: StringExpr, private val pos: NumericExpr, private val len: NumericExpr) :
    StringFunction("SEG$") {

    override fun value(): StringConstant {
        val original = str.value().toNative()
        val kotlinStart = pos.value().toNative().toInt() - 1
        val kotlinEnd = kotlinStart + len.value().toNative().toInt()
        return StringConstant(original.substring(kotlinStart, kotlinEnd))
    }

    override fun listArgs(): String = "$str,$pos,$len"
}

/**
 * The STR$ function returns a string equivalent to numeric-expression.
 * This allows the functions, statements, and commands that act on strings to be used on the character representation
 * of numeric-expression. The STR$ is the inverse of the [ValFunction].
 */
data class StrFunction(private val numericExpr: NumericExpr) : StringFunction("STR$") {
    override fun value() = StringConstant(numericExpr.displayValue())
    override fun listArgs(): String = numericExpr.listText()
}


fun toChar(asciiCode: Int): String {
    return asciiCode.toChar().toString()
}