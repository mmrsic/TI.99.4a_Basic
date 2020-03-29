package com.github.mmrsic.ti99.basic.expr

import com.github.mmrsic.ti99.basic.BadValue
import com.github.mmrsic.ti99.basic.NumberTooBig
import com.github.mmrsic.ti99.hw.TiBasicModule
import com.github.mmrsic.ti99.hw.TiBasicScreen
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sign

/** Number ranges as defined by the TI BASIC module. */
object NumberRanges {
    internal const val MIN_VALUE = 1e-128
    internal const val MAX_VALUE = 9.9999999999999e127
    private val OVERFLOW_VALUES = listOf(Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY)

    /** Whether a given [Number] is an overflow when used in calculations. */
    fun isOverflow(value: Number) = OVERFLOW_VALUES.contains(value.toDouble()) || abs(value.toDouble()) > MAX_VALUE

    /** Whether a given [Number] is an underflow (and treated silently as zero) when used in calculations. */
    fun isUnderflow(value: Number) = !isOverflow(value) && value != 0 && abs(value.toDouble()) < MIN_VALUE
}

/**
 * Numeric expressions are constructed from [NumericVariable]s, [NumericConstant]s, and function references using
 * arithmetic operators (+ - * / ^). All functions referenced in an expression must be either functions supplied by
 * TI BASIC or defined by a [DefStatement].
 */
abstract class NumericExpr : Expression {
    override fun displayValue(): String = value().displayValue()

    abstract override fun value(lambda: (value: Constant) -> Any): NumericConstant

    /** Check whether this numeric expression equals zero. */
    fun isZero(): Boolean = value().toNative() == 0.0
}

/** An [NumericExpr] representing a TI Basic arithmetic expression of exactly two numeric expressions. */
abstract class TwoOpNumericExpr(val op1: NumericExpr, val op2: NumericExpr) : NumericExpr() {
    override fun listText(): String = "${op1.listText()}${opSymbol()}${op2.listText().trim()}"

    override fun value(lambda: (value: Constant) -> Any): NumericConstant {
        val result = NumericConstant(executeNatively(op1.value(lambda).toNative(), op2.value(lambda).toNative()))
        lambda.invoke(result)
        return result
    }

    /**
     * Symbol of the operator used by this [TwoOpNumericExpr].
     * @return [String] representation of the operator symbol
     */
    abstract fun opSymbol(): String

    /** Execute this operation natively. */
    abstract fun executeNatively(op1: Number, op2: Number): Number
}

class Addition(op1: NumericExpr, op2: NumericExpr) : TwoOpNumericExpr(op1, op2) {
    override fun opSymbol(): String = "+"
    override fun executeNatively(op1: Number, op2: Number) = op1.toDouble() + op2.toDouble()
}

class Subtraction(op1: NumericExpr, op2: NumericExpr) : TwoOpNumericExpr(op1, op2) {
    override fun opSymbol(): String = "-"
    override fun executeNatively(op1: Number, op2: Number) = op1.toDouble() - op2.toDouble()
}

class Multiplication(op1: NumericExpr, op2: NumericExpr) : TwoOpNumericExpr(op1, op2) {
    override fun opSymbol(): String = "*"
    override fun executeNatively(op1: Number, op2: Number) = op1.toDouble() * op2.toDouble()
}

class Division(op1: NumericExpr, op2: NumericExpr) : TwoOpNumericExpr(op1, op2) {
    override fun opSymbol(): String = "/"
    override fun executeNatively(op1: Number, op2: Number) = op1.toDouble() / op2.toDouble()
}

class Exponentiation(op1: NumericExpr, op2: NumericExpr) : TwoOpNumericExpr(op1, op2) {
    override fun opSymbol(): String = "^"
    override fun executeNatively(op1: Number, op2: Number): Double {
        val baseValue = op1.toDouble()
        val expValue = op2.toDouble()
        if (baseValue < 0 && BigDecimal(expValue).scale() > 0) throw BadValue()
        return baseValue.pow(expValue)
    }
}

data class NegatedExpression(val original: NumericExpr) : NumericExpr() {
    override fun listText(): String = "-${original.listText()}"
    override fun value(lambda: (value: Constant) -> Any): NumericConstant {
        val result = NumericConstant(-original.value(lambda).toNative())
        lambda.invoke(result)
        return result
    }
}

data class NumericConstant(override val constant: Number) : NumericExpr(), Constant {
    /** Whether this [NumericConstant] represents a numeric overflow and will cause a warning. */
    val isOverflow = NumberRanges.isOverflow(constant)

    /** Whether this [NumericConstant] equals to [ZERO] because of numeric underflow. */
    val isUnderflow = NumberRanges.isUnderflow(constant)
    private val bigDecimal =
        if (isUnderflow || isOverflow) BigDecimal.ZERO else abs(constant.toDouble()).toBigDecimal().stripTrailingZeros()

    /** Whether this [NumericConstant] represents an integer. */
    val isInteger = bigDecimal.scale() <= 0

    override fun value(lambda: (value: Constant) -> Any): NumericConstant {
        lambda.invoke(this)
        return this
    }

    /** Always the native, that is, the [Double] value of this numeric constant. */
    override fun toNative(): Double =
        if (isOverflow) sign(constant.toDouble()) * NumberRanges.MAX_VALUE else toTiNumber(constant).toDouble()

    override fun displayValue(): String {
        val doubleValue = constant.toDouble()
        val signCharacter = if (doubleValue < 0) "-" else " "
        if (isUnderflow || doubleValue == 0.0) return " 0 "
        if (isOverflow || toNative() == NumberRanges.MAX_VALUE) return signCharacter + "9.99999E+**"
        return "$signCharacter${bigDecimalToTiString()} "
    }

    override fun listText(): String = displayValue().trim()

    companion object {
        /** Numeric constant of value zero. */
        val ZERO = NumericConstant(0)

        /** Numeric constant of value one. */
        val ONE = NumericConstant(1)
    }

    // HELPERS //

    /** Convert [bigDecimal] to a string in TI Basic output style. */
    private fun bigDecimalToTiString(): String {
        val scale = bigDecimal.scale()
        val precision = bigDecimal.precision()
        val numDigits = if (scale >= 0) max(scale, precision) else precision - scale
        val isScientific = numDigits > 10 && (isInteger || (scale > precision && numDigits >= 10))
        return if (isScientific) {
            val nativeScientific = "%.5E".format(Locale.US, bigDecimal)
            nativeScientific.replace(Regex("(0+E)"), "E").replace(Regex("""E([+-])\d{3}"""), "E$1**")
        } else {
            val properScale = if (numDigits > 10 && scale > 0) scale + 10 - numDigits else bigDecimal.scale()
            val scaledDecimal = bigDecimal.setScale(properScale, RoundingMode.HALF_UP).toPlainString()
            (if (properScale > 0) scaledDecimal.trimEnd('0') else scaledDecimal).replace(Regex("^0."), ".")
        }
    }
}

data class NumericVariable(val name: String, val calc: (String) -> NumericConstant) : NumericExpr() {
    override fun listText(): String = name
    override fun value(lambda: (value: Constant) -> Any): NumericConstant = calc.invoke(name)
}

class NumericArrayAccess(
    val baseName: String,
    val arrayIndex: NumericExpr, machine: TiBasicModule
) : NumericExpr(), TiBasicModule.Dependent {

    override val basicModule = machine
    override fun listText() = "$baseName(${arrayIndex.listText()})"
    override fun value(lambda: (value: Constant) -> Any): NumericConstant {
        return basicModule.getNumericArrayVariableValue(baseName, arrayIndex)
    }
}

object RelationalExpr {

    /** Operator of a [RelationalExpr]. */
    enum class Operator {
        EQUAL_TO, NOT_EQUAL_TO, LESS_THAN, LESS_THAN_OR_EQUAL_TO, GREATER_THAN, GREATER_THAN_OR_EQUAL_TO;

        companion object {
            /** Create a [Operator] from a given symbol representing it. */
            fun fromSymbol(symbol: String): Operator {
                return when (symbol) {
                    "=" -> EQUAL_TO
                    "<>" -> NOT_EQUAL_TO
                    "<" -> LESS_THAN
                    "<=" -> LESS_THAN_OR_EQUAL_TO
                    ">" -> GREATER_THAN
                    ">=" -> GREATER_THAN_OR_EQUAL_TO
                    else -> throw IllegalArgumentException("No relational operator symbol: $symbol")
                }
            }
        }
    }
}

class RelationalNumericExpr(val a: NumericExpr, val op: RelationalExpr.Operator, val b: NumericExpr) :
    TwoOpNumericExpr(a, b) {

    override fun opSymbol(): String = op.name
    override fun executeNatively(aNative: Number, bNative: Number): Number {
        val isTrue: Boolean = when (op) {
            RelationalExpr.Operator.EQUAL_TO -> aNative == bNative
            RelationalExpr.Operator.NOT_EQUAL_TO -> aNative != bNative
            RelationalExpr.Operator.LESS_THAN -> aNative.toDouble() < bNative.toDouble()
            RelationalExpr.Operator.LESS_THAN_OR_EQUAL_TO -> aNative.toDouble() <= bNative.toDouble()
            RelationalExpr.Operator.GREATER_THAN -> aNative.toDouble() > bNative.toDouble()
            RelationalExpr.Operator.GREATER_THAN_OR_EQUAL_TO -> aNative.toDouble() >= bNative.toDouble()
        }
        return if (isTrue) -1 else 0
    }
}

class RelationalStringExpr(val a: StringExpr, val op: RelationalExpr.Operator, val b: StringExpr) : NumericExpr() {

    override fun value(lambda: (value: Constant) -> Any): NumericConstant {
        val aNative = a.value(lambda).toNative()
        val bNative = b.value(lambda).toNative()
        val isTrue: Boolean = when (op) {
            RelationalExpr.Operator.EQUAL_TO -> aNative == bNative
            RelationalExpr.Operator.NOT_EQUAL_TO -> aNative != bNative
            RelationalExpr.Operator.LESS_THAN -> aNative < bNative
            RelationalExpr.Operator.LESS_THAN_OR_EQUAL_TO -> aNative <= bNative
            RelationalExpr.Operator.GREATER_THAN -> aNative > bNative
            RelationalExpr.Operator.GREATER_THAN_OR_EQUAL_TO -> aNative >= bNative
        }
        return NumericConstant(if (isTrue) -1 else 0)
    }

    override fun listText(): String = "${a.listText()}$op${b.listText()}"

}

/**
 * The TAB function specifies the starting position on the print-line for the next print item. The numeric-expression
 * is evaluated and rounded to the nearest integer n. If n is less than one, then its value is replaced by one. If n
 * is greater than 28, then n is repeatedly reduced by 28 until 1 <= n <= 28.
 * If the number of characters already printed on the current line is greater than n, then the next item is printed on
 * the next line beginning in position n.
 */
data class TabFunction(private val numericExpr: NumericExpr) : NumericFunction("TAB") {
    override fun value(lambda: (value: Constant) -> Any): NumericConstant {
        val n = max(1, numericExpr.value().toNative().roundToInt())
        val result = (n - 1) % TiBasicScreen.NUM_PRINT_COLUMNS + 1
        return NumericConstant(result)
    }

    override fun listArgs() = numericExpr.listText()
}

// HELPERS //

private fun toTiNumber(original: Number): Number {
    if (original is Int) return original
    val rounded = "%.10f".format(Locale.US, original).toDouble()
    val asInt = original.toInt()
    if (asInt.compareTo(rounded) == 0) return original
    if (NumberRanges.isOverflow(rounded)) throw NumberTooBig()
    return if (NumberRanges.isUnderflow(rounded)) 0 else rounded
}
