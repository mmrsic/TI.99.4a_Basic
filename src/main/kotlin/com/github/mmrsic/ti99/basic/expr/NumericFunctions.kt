package com.github.mmrsic.ti99.basic.expr

import kotlin.math.*
import kotlin.random.Random

abstract class NumericFunction(val name: String) : NumericExpr() {
    override fun listText() = "$name(${listArgs()})"
    abstract fun listArgs(): String
}

/**
 * The ABS function gives the absolute value of numeric-expression.
 * If numeric-expression is positive, ABS gives the value of numeric-expression. If numeric-expression is negative,
 * ABS gives its negative (a positive number). If numeric-expression is zero, ABS returns zero.
 * The result of ABS is always a non-negative number.
 */
data class AbsFunction(private val numericExpr: NumericExpr) : NumericFunction("ABS") {
    override fun value() = NumericConstant(abs(numericExpr.value().toNative()))
    override fun listArgs() = numericExpr.listText()
}

/**
 * The ASC function gives the ASCII character code which corresponds to the first character of string-expression.
 * The ASC function is the inverse of the [ChrFunction] function
 */
data class AscFunction(private val stringExpr: StringExpr) : NumericFunction("ASC") {
    override fun value(): NumericConstant = NumericConstant(toAsciiCode(stringExpr.value().toNative()[0]))
    override fun listArgs() = stringExpr.listText()
}

/**
 * The ATN function returns the measure of the angle (in radians) whose tangent is numeric-expression.
 * If you want the equivalent angle in degrees, multiply by 180/PI. The value given by the ATN function is always
 * in the range -PI/2 < ATN(X) < PI/2.
 */
data class AtnFunction(private val numericExpr: NumericExpr) : NumericFunction("ATN") {
    override fun value() = NumericConstant(atan(numericExpr.value().toNative()))
    override fun listArgs() = numericExpr.listText()
}

/**
 * The COS function gives the trigonometric cosine of radian-expression.
 * If the angle is in degrees, multiply the number of degrees by PI/180 to get the equivalent angle in radians.
 */
data class CosFunction(private val radianExpr: NumericExpr) : NumericFunction("COS") {
    override fun value() = NumericConstant(cos(radianExpr.value().toNative()))
    override fun listArgs() = radianExpr.listText()
}

/**
 * The EXP function returns the exponential value (e^x) of numeric-expression.
 * The value of e is 2.718281828459.
 */
data class ExpFunction(private val numericExpr: NumericExpr) : NumericFunction("EXP") {
    override fun value() = NumericConstant(exp(numericExpr.value().toNative()))
    override fun listArgs() = numericExpr.listText()
}

/**
 * The INT function returns the greatest integer less than or equal to numeric-expression.
 */
data class IntFunction(private val numericExpr: NumericExpr) : NumericFunction("INT") {
    override fun value() = NumericConstant(floor(numericExpr.value().toNative()).toInt())
    override fun listArgs() = numericExpr.listText()
}


/**
 * The LEN function returns the number of characters in string-expression.
 * A space counts as a character.
 */
data class LenFunction(private val stringExpr: StringExpr) : NumericFunction("LEN") {
    override fun value() = NumericConstant(stringExpr.value().toNative().length)
    override fun listArgs() = stringExpr.listText()
}

/**
 * The LOG function returns the natural logarithm of numeric-expression where numeric-expression is greater than zero.
 * The LOG function is the inverse of [ExpFunction].
 */
data class LogFunction(private val numericExpr: NumericExpr) : NumericFunction("LOG") {
    override fun value() = NumericConstant(ln(numericExpr.value().toNative()))
    override fun listArgs() = numericExpr.listText()
}

/**
 * The POS function returns the position of the first occurrence of string2 in string1.
 * The search begins at the position specified by numeric-expression. If no match is found, the function returns
 * a value of zero.
 */
data class PosFunction(private val str1: StringExpr, private val str2: StringExpr, private val pos: NumericExpr) :
    NumericFunction("POS") {
    override fun value(): NumericConstant {
        val source = str1.value().toNative()
        val searchString = str2.value().toNative()
        val startIndex = pos.value().toNative().toInt()
        return NumericConstant(1 + source.indexOf(searchString, startIndex))
    }

    override fun listArgs() = "${str1.listText()},${str2.listText()},$pos"
}

/**
 * The RND function returns the next pseudo-random number in the current sequence of pseudo-random numbers.
 * The number returned is greater than or equal to zero and less than one. The sequence of random numbers returned
 * is the same every time a program is run unless the randomize statement appears in the program.
 */
class RndFunction : NumericFunction("RND") {
    private val generator: Random = Random(42)
    override fun value() = NumericConstant(generator.nextDouble())
    override fun listArgs() = ""
}

/**
 * The SGN function returns 1 if numeric-expression is positive, 0 if it is zero, and -1 if it is negative.
 */
data class SgnFunction(private val numericExpr: NumericExpr) : NumericFunction("SGN") {
    override fun value() = NumericConstant(sign(numericExpr.value().toNative()))
    override fun listArgs() = numericExpr.listText()
}

/**
 * The SIN function gives the trigonometric sine of radian-expression.
 * If the angle is in degrees, multiply the number of degrees by ATN(1)/45 to get the equivalent angle in radians.
 */
data class SinFunction(private val numericExpr: NumericExpr) : NumericFunction("SIN") {
    override fun value() = NumericConstant(sin(numericExpr.value().toNative()))
    override fun listArgs() = numericExpr.listText()
}

/**
 * The SQR function returns the positive square root of numeric-expression.
 * SQR(X) is equivalent to X^(1/2). Numeric-expression may not be a negative number.
 */
data class SqrFunction(private val numericExpr: NumericExpr) : NumericFunction("SQR") {
    override fun value() = NumericConstant(sqrt(numericExpr.value().toNative()))
    override fun listArgs() = numericExpr.listText()
}


/** Convert a given character into its corresponding ASCII code. */
fun toAsciiCode(c: Char) = c.toInt()
