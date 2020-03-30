package com.github.mmrsic.ti99.basic.expr

import com.github.mmrsic.ti99.basic.BadArgument
import com.github.mmrsic.ti99.basic.BadValue
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * A numeric function is a TI Basic function which returns a [NumericExpr].
 */
abstract class NumericFunction(val name: String) : NumericExpr() {
    override fun listText() = "$name(${listArgs()})"

    /** Argument(s) description of this numeric expression as given by the LIST command. */
    abstract fun listArgs(): String
}

/**
 * The ABS function gives the absolute value of numeric-expression.
 * If numeric-expression is positive, ABS gives the value of numeric-expression. If numeric-expression is negative,
 * ABS gives its negative (a positive number). If numeric-expression is zero, ABS returns zero.
 * The result of ABS is always a non-negative number.
 */
data class AbsFunction(private val numericExpr: NumericExpr) : NumericFunction("ABS") {
    override fun value(lambda: (value: Constant) -> Any): NumericConstant {
        val result = NumericConstant(abs(numericExpr.value(lambda).toNative()))
        lambda.invoke(result)
        return result
    }

    override fun listArgs() = numericExpr.listText()
}

/**
 * The ASC function gives the ASCII character code which corresponds to the first character of string-expression.
 * The ASC function is the inverse of the [ChrFunction] function
 */
data class AscFunction(private val stringExpr: StringExpr) : NumericFunction("ASC") {
    override fun value(lambda: (value: Constant) -> Any): NumericConstant {
        val result = NumericConstant(toAsciiCode(stringExpr.value(lambda).toNative()[0]))
        lambda.invoke(result)
        return result
    }

    override fun listArgs() = stringExpr.listText()
}

/**
 * The ATN function returns the measure of the angle (in radians) whose tangent is numeric-expression.
 * If you want the equivalent angle in degrees, multiply by 180/PI. The value given by the ATN function is always
 * in the range -PI/2 < ATN(X) < PI/2.
 */
data class AtnFunction(private val numericExpr: NumericExpr) : NumericFunction("ATN") {
    override fun value(lambda: (value: Constant) -> Any): NumericConstant {
        val result = NumericConstant(atan(numericExpr.value(lambda).toNative()))
        lambda.invoke(result)
        return result
    }

    override fun listArgs() = numericExpr.listText()
}

/**
 * The COS function gives the trigonometric cosine of radian-expression.
 * If the angle is in degrees, multiply the number of degrees by PI/180 to get the equivalent angle in radians.
 */
data class CosFunction(private val radianExpr: NumericExpr) : NumericFunction("COS") {
    override fun value(lambda: (value: Constant) -> Any): NumericConstant {
        val arg = radianExpr.value(lambda).toNative()
        if (abs(arg) >= maxArgTrigonometricFunction) throw BadArgument()
        val result = NumericConstant(cos(arg))
        lambda.invoke(result)
        return result
    }

    override fun listArgs() = radianExpr.listText()
}

/**
 * The EXP function returns the exponential value (e^x) of numeric-expression. The value of e is 2.718281828459. The
 * exponential function is the inverse of the natural logarithm function ([LogFunction]). Thus, X = EXP(LOG(X)).
 */
data class ExpFunction(private val numericExpr: NumericExpr) : NumericFunction("EXP") {
    override fun value(lambda: (value: Constant) -> Any): NumericConstant {
        val result = NumericConstant(exp(numericExpr.value(lambda).toNative()))
        lambda.invoke(result)
        return result
    }

    override fun listArgs() = numericExpr.listText()
}

/**
 * The INT function returns the greatest integer less than or equal to numeric-expression.
 */
data class IntFunction(private val numericExpr: NumericExpr) : NumericFunction("INT") {
    override fun value(lambda: (value: Constant) -> Any): NumericConstant {
        val result = NumericConstant(floor(numericExpr.value(lambda).toNative()).toInt())
        lambda.invoke(result)
        return result
    }

    override fun listArgs() = numericExpr.listText()
}

/**
 * The LEN function returns the number of characters in string-expression.
 * A space counts as a character. The length of a null string is zero.
 */
data class LenFunction(private val stringExpr: StringExpr) : NumericFunction("LEN") {
    override fun value(lambda: (value: Constant) -> Any): NumericConstant {
        val result = NumericConstant(stringExpr.value(lambda).toNative().length)
        lambda.invoke(result)
        return result
    }

    override fun listArgs() = stringExpr.listText()
}

/**
 * The LOG function returns the natural logarithm of numeric-expression where numeric-expression is greater than zero.
 * The LOG function is the inverse of the [ExpFunction]. Thus, X = LOG(EXP(X)).
 *
 * The argument of the natural logarithm must be greater than zero, otherwise the message "BAD ARGUMENT" is displayed.
 */
data class LogFunction(private val numericExpr: NumericExpr) : NumericFunction("LOG") {
    companion object {
        /** Lower bound for [numericExpr] value. */
        const val lowerBoundValue = 0
    }

    override fun value(lambda: (value: Constant) -> Any): NumericConstant {
        val arg = numericExpr.value(lambda).toNative()
        if (arg <= lowerBoundValue) throw BadArgument()
        val result = NumericConstant(ln(arg))
        lambda.invoke(result)
        return result
    }

    override fun listArgs() = numericExpr.listText()
}

/**
 * The POS function returns the position of the first occurrence of string2 in string1.
 * The search begins at the position specified by numeric-expression, which is evaluated and rounded, if necessary, to
 * obtain an integer. If no match is found, the function returns a value of zero. If the value specified for pos is less
 * than zero, [BadValue] is thrown.
 */
data class PosFunction(private val str1: StringExpr, private val str2: StringExpr, private val pos: NumericExpr) :
    NumericFunction("POS") {
    override fun value(lambda: (value: Constant) -> Any): NumericConstant {
        val source = str1.value(lambda).toNative()
        val searchString = str2.value(lambda).toNative()
        val startIndex = pos.value(lambda).toNative().roundToInt()
        if (startIndex < 0) throw BadValue()
        val result = NumericConstant(1 + source.indexOf(searchString, startIndex - 1))
        lambda.invoke(result)
        return result
    }

    override fun listArgs() = "${str1.listText()},${str2.listText()},$pos"
}

/**
 * The RND function returns the next pseudo-random number in the current sequence of pseudo-random numbers.
 * The number returned is greater than or equal to zero and less than one. The sequence of random numbers returned
 * is the same every time a program is run unless the randomize statement appears in the program.
 */
class RndFunction(private val randomGenerator: () -> Double) : NumericFunction("RND") {
    override fun value(lambda: (value: Constant) -> Any): NumericConstant = NumericConstant(randomGenerator.invoke())
    override fun listArgs() = ""
}

/**
 * The SGN function returns 1 if [numericExpr] is positive, 0 if it is zero, and -1 if it is negative.
 */
data class SgnFunction(private val numericExpr: NumericExpr) : NumericFunction("SGN") {
    override fun value(lambda: (value: Constant) -> Any): NumericConstant {
        val result = NumericConstant(sign(numericExpr.value(lambda).toNative()))
        lambda.invoke(result)
        return result
    }

    override fun listArgs() = numericExpr.listText()
}

/**
 * The SIN function gives the trigonometric sine of radian-expression.
 * If the angle is in degrees, multiply the number of degrees by ATN(1)/45 to get the equivalent angle in radians.
 */
data class SinFunction(private val radianExpr: NumericExpr) : NumericFunction("SIN") {
    override fun value(lambda: (value: Constant) -> Any): NumericConstant {
        val arg = radianExpr.value(lambda).toNative()
        if (abs(arg) >= maxArgTrigonometricFunction) throw BadArgument()
        val result = NumericConstant(sin(arg))
        lambda.invoke(result)
        return result
    }

    override fun listArgs() = radianExpr.listText()
}

/**
 * The SQR function returns the positive square root of numeric-expression.
 * SQR(X) is equivalent to X^(1/2). Numeric-expression may not be a negative number.
 */
data class SqrFunction(private val numericExpr: NumericExpr) : NumericFunction("SQR") {
    override fun value(lambda: (value: Constant) -> Any): NumericConstant {
        val arg = numericExpr.value(lambda).toNative()
        if (arg < 0) throw BadArgument()
        val result = NumericConstant(sqrt(arg))
        lambda.invoke(result)
        return result
    }

    override fun listArgs() = numericExpr.listText()
}

/**
 * The TAN function gives the trigonometric tangent of [radianExpr]. If the angle is in degrees, multiply the number of
 * degrees by PI/180 to get the equivalent angle in radians. You may use (4*ATN(1))/180 for PI/180.
 *
 * Note that BAD ARGUMENT is displayed and the program stops running if the value of the radian expression is greater
 * than or equal to [maxArgTrigonometricFunction].
 */
data class TanFunction(private val radianExpr: NumericExpr) : NumericFunction("TAN") {
    override fun value(lambda: (value: Constant) -> Any): NumericConstant {
        val arg = radianExpr.value(lambda).toNative()
        if (arg >= maxArgTrigonometricFunction) throw BadArgument()
        val result = NumericConstant(tan(arg))
        lambda.invoke(result)
        return result
    }

    override fun listArgs() = radianExpr.listText()
}

/**
 * The VAL function is the inverse of the [StrFunction]. If the string specified by [stringExpr] is a valid
 * representation of a numeric constant, then the value function converts the string to a numeric constant.
 * If the string specified is not a valid representation of a number or if the string is of zero length, [BadArgument]
 * is thrown. If you specify a string which is longer than 254 characters, [BadArgument] is thrown.
 */
data class ValFunction(private val stringExpr: StringExpr) : NumericFunction("VAL") {
    override fun value(lambda: (value: Constant) -> Any): NumericConstant {
        val arg = stringExpr.value(lambda).toNative()
        if (arg.isEmpty() || arg.length > 254) throw BadArgument()
        try {
            return NumericConstant(arg.toDouble())
        } catch (e: Exception) {
            throw BadArgument()
        }
    }

    override fun listArgs() = stringExpr.listText()
}


/** Convert a given character into its corresponding ASCII code. */
fun toAsciiCode(c: Char) = c.toInt()

/** Maximum allowed value for argument of trigonometric functions (according to User's Reference Guide). */
private val maxArgTrigonometricFunction = 1.5707963266375 * (10.0.pow(10))

