package com.github.mmrsic.ti99.basic.expr

import com.github.mmrsic.ti99.basic.BadValue
import com.github.mmrsic.ti99.basic.NumberTooBig
import com.github.mmrsic.ti99.basic.StringNumberMismatch
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.math.*

object NumberRanges {
    internal const val MIN_VALUE = 1e-128
    internal const val MAX_VALUE = 9.9999999999999e127

    fun isOverflow(value: Number): Boolean {
        return value == Double.NEGATIVE_INFINITY || value == Double.NaN as Number || abs(value.toDouble()) > MAX_VALUE
    }

    fun isUnderflow(value: Number): Boolean {
        return value != 0 && abs(value.toDouble()) < MIN_VALUE
    }
}

abstract class NumericExpr : Expression {
    abstract override fun value(): NumericConstant
    override fun displayValue(): String = value().displayValue()
    open fun visitAllValues(lambda: (value: NumericConstant) -> Any) = lambda.invoke(value())
    /** Check whether this numeric expression equals zero. */
    fun isZero(): Boolean = value().toNative() == 0.0
}

/** An [NumericExpr] representing a TI Basic arithmetic expression of exactly two numeric expressions. */
abstract class TwoOpNumericExpr(val op1: NumericExpr, val op2: NumericExpr) : NumericExpr() {

    override fun visitAllValues(lambda: (value: NumericConstant) -> Any): Any {
        lambda.invoke(op1.value())
        lambda.invoke(op2.value())
        return super.visitAllValues(lambda)
    }

    override fun listText(): String = "${op1.listText()}${opSymbol()}${op2.listText().trim()}"

    /**
     * Symbol of the operator used by this [TwoOpNumericExpr].
     * @return [String] representation of the operator symbol
     */
    abstract fun opSymbol(): String
}

class Addition(op1: NumericExpr, op2: NumericExpr) : TwoOpNumericExpr(op1, op2) {
    override fun value() = NumericConstant(op1.value().toNative() + op2.value().toNative())
    override fun opSymbol(): String = "+"
}

class Subtraction(op1: NumericExpr, op2: NumericExpr) : TwoOpNumericExpr(op1, op2) {
    override fun value() = NumericConstant(op1.value().toNative() - op2.value().toNative())
    override fun opSymbol(): String = "-"
}

class Multiplication(op1: NumericExpr, op2: NumericExpr) : TwoOpNumericExpr(op1, op2) {
    override fun value() = NumericConstant(op1.value().toNative() * op2.value().toNative())
    override fun opSymbol(): String = "*"
}

class Division(op1: NumericExpr, op2: NumericExpr) : TwoOpNumericExpr(op1, op2) {
    override fun value() = NumericConstant(op1.value().toNative() / op2.value().toNative())
    override fun opSymbol(): String = "/"
}

class Exponentiation(op1: NumericExpr, op2: NumericExpr) : TwoOpNumericExpr(op1, op2) {
    override fun value(): NumericConstant {
        val baseValue = op1.value().toNative()
        val expValue = op2.value().toNative()
        if (baseValue < 0 && BigDecimal(expValue).scale() > 0) throw BadValue()
        return NumericConstant(baseValue.pow(expValue))
    }

    override fun opSymbol(): String = "^"
}

data class NegatedExpression(val original: NumericExpr) : NumericExpr() {
    override fun value() = NumericConstant(-original.value().toNative())
    override fun listText(): String = "-${original.listText()}"
}

data class NumericConstant(private val constant: Number) : NumericExpr(), Constant {
    val isOverflow = NumberRanges.isOverflow(constant)
    val isUnderflow = NumberRanges.isUnderflow(constant)
    val isInteger = constant.toDouble().roundToInt().toDouble() == constant.toDouble()
    val isExponential =
        listOf(Double.NaN, Double.NEGATIVE_INFINITY).contains(constant) || constant.toString().contains("E", true)

    override fun value(): NumericConstant = this

    /** Always the native, that is, the [Double] value of this numeric constant. */
    override fun toNative(): Double =
        if (isOverflow) sign(constant.toDouble()) * NumberRanges.MAX_VALUE else toTiNumber(constant).toDouble()

    override fun displayValue(): String {
        val doubleValue = constant.toDouble()
        val signCharacter = if (doubleValue < 0) "-" else " "
        if (isUnderflow) {
            return " 0 "
        }
        if (isOverflow) {
            return signCharacter + "9.99999E+**"
        }
        if (isInteger) {
            return signCharacter + abs(constant.toInt()) + " "
        }
        if (isExponential) {
            val positiveSign = if (doubleValue < 0) "" else " "
            val bigDecimal = BigDecimal(doubleValue)
            val scaledDecimal = bigDecimal.setScale(-(bigDecimal.precision() - 9), RoundingMode.HALF_UP)
            return positiveSign + scaledDecimal.toString().replace(Regex("(0+E)"), "E")
        }

        val numDigitsBeforeDot = if (doubleValue == 0.0) 0 else log10(abs(doubleValue)).toInt()
        val numDigits = 9 - numDigitsBeforeDot + (if (abs(doubleValue) < 1) 1 else 0)
        val formatSpec = "%." + numDigits + "f"
        val numText = formatSpec.format(Locale.US, doubleValue).trimEnd('0').replace(Regex("^0+"), "")
        return if (doubleValue < 0) "$numText " else " $numText "
    }

    override fun listText(): String = displayValue().trim()
}

data class NumericVariable(val name: String, val calc: (String) -> NumericConstant) : NumericExpr() {
    override fun value(): NumericConstant = calc.invoke(name)
    override fun listText(): String = name
}

object RelationalExpr {
    fun create(a: Expression, op: Operator, b: Expression): NumericExpr {
        if (a is NumericExpr && b is NumericExpr) {
            return RelationalNumericExpr(a, op, b)
        }
        if (a is StringExpr && b is StringExpr) {
            return RelationalStringExpr(a, op, b)
        }
        if (a is StringExpr && b is NumericExpr) {
            return StringNumberMismatchExpr(a, b)
        }
        if (a is NumericExpr && b is StringExpr) {
            return StringNumberMismatchExpr(a, b)
        }
        throw IllegalStateException("Logic error in method: Not all possible combinations covered")
    }

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

    override fun value(): NumericConstant {
        val aNative = a.value().toNative()
        val bNative = b.value().toNative()
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

    override fun opSymbol(): String = op.name

}

class RelationalStringExpr(val a: StringExpr, val op: RelationalExpr.Operator, val b: StringExpr) : NumericExpr() {

    override fun value(): NumericConstant {
        val aNative = a.value().toNative()
        val bNative = b.value().toNative()
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

class StringNumberMismatchExpr(val a: NumericExpr, val b: StringExpr) : NumericExpr() {
    constructor(a: StringExpr, b: NumericExpr) : this(b, a)

    override fun value() = throw StringNumberMismatch()
    override fun displayValue() = throw StringNumberMismatch()
    override fun listText(): String = a.listText() + b.listText()
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
