package com.github.mmrsic.ti99.basic.expr

import com.github.mmrsic.ti99.basic.BadValue
import kotlin.math.roundToInt

abstract class StringFunction(val name: String) : StringExpr() {
    override fun listText(): String {
        return "$name(${listArgs()})"
    }

    /** Arguments of this function as provided by the LIST statement. */
    abstract fun listArgs(): String
}

/**
 * The CHR$ function returns the character corresponding to the ASCII character code specified by numeric-expression.
 * The CHR$ function is the inverse of the [AscFunction].
 * If the [numericExpr] is not an integer, it is rounded to obtain an integer.
 * If the argument specified is a value between 32 and 127, inclusive, a standard character is given. If the argument
 * specified is between 128 and 159, inclusive, and a special graphics character has been defined for that value, the
 * graphics character is given. If you specify an argument which designates an undefined character (i.e. not a standard
 * character or a defined graphics character), then the character given is whatever is in memory at that time.
 * @param numericExpr must be between zero and 32767, otherwise [BadValue] is thrown
 */
data class ChrFunction(private val numericExpr: NumericExpr) : StringFunction("CHR$") {
    override fun value(lambda: (value: Constant) -> Any): StringConstant {
        val arg = numericExpr.value().toNative().roundToInt()
        if (arg !in 0..32767) throw BadValue()
        return StringConstant(toChar(arg))
    }

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

    override fun value(lambda: (value: Constant) -> Any): StringConstant {
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
    override fun value(lambda: (value: Constant) -> Any): StringConstant = StringConstant(numericExpr.displayValue())
    override fun listArgs(): String = numericExpr.listText()
}


/** Convert a given ASCII code into a string containing the character associated with the code. */
fun toChar(asciiCode: Int): String {
    return asciiCode.toChar().toString()
}

/** Remove all surrounding quotes of a given string. */
fun unquote(text: String?): String? {
    return text?.removePrefix("\"")?.removeSuffix("\"")
}