package com.github.mmrsic.ti99.basic.expr

import kotlin.math.*
import kotlin.random.Random

abstract class NumericFunction(val name: String) : NumericExpr()

/**
 * The ABS function gives the absolute value of numeric-expression.
 * If numeric-expression is positive, ABS gives the value of numeric-expression. If numeric-expression is negative,
 * ABS gives its negative (a positive number). If numeric-expression is zero, ABS returns zero.
 * The result of ABS is always a non-negative number.
 */
data class AbsFunction(private val numericExpr: NumericExpr) : NumericFunction("ABS") {
    override fun calculate(): Number = abs(numericExpr.calculate().toDouble())
}

/**
 * The ASC function gives the ASCII character code which corresponds to the first character of string-expression.
 * The ASC function is the inverse of the [ChrFunction] function
 */
data class AscFunction(private val stringExpr: StringExpr) : NumericFunction("ASC") {
    override fun calculate(): Int = toAsciiCode(stringExpr.calculate()[0])

}

/**
 * The ATN function returns the measure of the angle (in radians) whose tangent is numeric-expression.
 * If you want the equivalent angle in degrees, multiply by 180/PI. The value given by the ATN function is always
 * in the range -PI/2 < ATN(X) < PI/2.
 */
data class AtnFunction(private val numericExpr: NumericExpr) : NumericFunction("ATN") {
    override fun calculate(): Number = atan(numericExpr.calculate().toDouble())
}

/**
 * The COS function gives the trigonometric cosine of radian-expression.
 * If the angle is in degrees, multiply the number of degrees by PI/180 to get the equivalent angle in radians.
 */
data class CosFunction(private val radianExpr: NumericExpr) : NumericFunction("COS") {
    override fun calculate(): Number = cos(radianExpr.calculate().toDouble())
}

/**
 * The EXP function returns the exponential value (e^x) of numeric-expression.
 * The value of e is 2.718281828459.
 */
data class ExpFunction(private val numericExpr: NumericExpr) : NumericFunction("EXP") {
    override fun calculate(): Number = exp(numericExpr.calculate().toDouble())
}

/**
 * The INT function returns the greatest integer less than or equal to numeric-expression.
 */
data class IntFunction(private val numericExpr: NumericExpr) : NumericFunction("INT") {
    override fun calculate(): Int = floor(numericExpr.calculate().toDouble()).toInt()
}


/**
 * The LEN function returns the number of characters in string-expression.
 * A space counts as a character.
 */
data class LenFunction(private val stringExpr: StringExpr) : NumericFunction("LEN") {
    override fun calculate(): Int = stringExpr.calculate().length
}

/**
 * The LOG function returns the natural logarithm of numeric-expression where numeric-expression is greater than zero.
 * The LOG function is the inverse of [ExpFunction].
 */
data class LogFunction(private val numericExpr: NumericExpr) : NumericFunction("LOG") {
    override fun calculate(): Number = ln(numericExpr.calculate().toDouble())
}

/**
 * The POS function returns the position of the first occurrence of string2 in string1.
 * The search begins at the position specified by numeric-expression. If no match is found, the function returns
 * a value of zero.
 */
data class PosFunction(
    private val string1: StringExpr,
    private val string2: StringExpr,
    private val numericExpr: NumericExpr
) : NumericFunction("POS") {
    override fun calculate(): Number =
        1 + string1.calculate().indexOf(string2.calculate(), numericExpr.calculate().toInt())
}

/**
 * The RND function returns the next pseudo-random number in the current sequence of pseudo-random numbers.
 * The number returned is greater than or equal to zero and less than one. The sequence of random numbers returned
 * is the same every time a program is run unless the randomize statement appears in the program.
 */
class RndFunction() : NumericFunction("RND") {
    private val generator: Random = Random(42)
    override fun calculate(): Number = generator.nextDouble()
}

/**
 * The SGN function returns 1 if numeric-expression is positive, 0 if it is zero, and -1 if it is negative.
 */
data class SgnFunction(private val numericExpr: NumericExpr) : NumericFunction("SGN") {
    override fun calculate(): Number = sign(numericExpr.calculate().toDouble()).toInt()
}

/**
 * The SIN function gives the trigonometric sine of radian-expression.
 * If the angle is in degrees, multiply the number of degrees by ATN(1)/45 to get the equivalent angle in radians.
 */
data class SinFunction(private val numericExpr: NumericExpr) : NumericFunction("SIN") {
    override fun calculate(): Number = sin(numericExpr.calculate().toDouble())
}

/**
 * The SQR function returns the positive square root of numeric-expression.
 * SQR(X) is equivalent to X^(1/2). Numeric-expression may not be a negative number.
 */
data class SqrFunction(private val numericExpr: NumericExpr) : NumericFunction("SQR") {
    override fun calculate(): Number = sqrt(numericExpr.calculate().toDouble())
}


/**
 * Convert a given character into its corresponding ASCII code.
 */
fun toAsciiCode(c: Char): Int {
    return c.toInt()
}
