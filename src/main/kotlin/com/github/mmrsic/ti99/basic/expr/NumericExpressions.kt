package com.github.mmrsic.ti99.basic.expr

import com.github.mmrsic.ti99.basic.BadValue
import com.github.mmrsic.ti99.basic.NumberTooBig
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
}

abstract class TwoOpNumericExpr(val op1: NumericExpr, val op2: NumericExpr) : NumericExpr() {
    override fun visitAllValues(lambda: (value: NumericConstant) -> Any): Any {
        lambda.invoke(op1.value())
        lambda.invoke(op2.value())
        return super.visitAllValues(lambda)
    }
}

class Addition(op1: NumericExpr, op2: NumericExpr) : TwoOpNumericExpr(op1, op2) {
    override fun value() = NumericConstant(op1.value().toNative() + op2.value().toNative())
}

class Subtraction(op1: NumericExpr, op2: NumericExpr) : TwoOpNumericExpr(op1, op2) {
    override fun value() = NumericConstant(op1.value().toNative() - op2.value().toNative())
}

class Multiplication(op1: NumericExpr, op2: NumericExpr) : TwoOpNumericExpr(op1, op2) {
    override fun value() = NumericConstant(op1.value().toNative() * op2.value().toNative())
}

class Division(op1: NumericExpr, op2: NumericExpr) : TwoOpNumericExpr(op1, op2) {
    override fun value() = NumericConstant(op1.value().toNative() / op2.value().toNative())
}

class Exponentiation(op1: NumericExpr, op2: NumericExpr) : TwoOpNumericExpr(op1, op2) {
    override fun value(): NumericConstant {
        val baseValue = op1.value().toNative()
        val expValue = op2.value().toNative()
        if (baseValue < 0 && expValue is Double && BigDecimal(expValue).scale() > 0) {
            throw BadValue()
        }
        return NumericConstant(baseValue.pow(expValue))
    }
}

data class NegatedExpression(val original: NumericExpr) : NumericExpr() {
    override fun value() = NumericConstant(-original.value().toNative())
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
            return signCharacter + abs(constant.toInt()).toString() + " "
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
}

data class NumericVariable(val name: String, val calc: (String) -> NumericConstant) : NumericExpr() {
    override fun value(): NumericConstant = calc.invoke(name)
}


// HELPERS //

private fun toTiNumber(original: Number): Number {
    if (original is Int) {
        return original
    }
    val rounded = "%.10f".format(Locale.US, original).toDouble()
    val asInt = original.toInt()
    if (asInt.compareTo(rounded) == 0) {
        return original
    }
    if (NumberRanges.isOverflow(rounded)) {
        throw NumberTooBig(if (rounded < 0) -1 * NumberRanges.MAX_VALUE else NumberRanges.MAX_VALUE)
    }
    return if (NumberRanges.isUnderflow(rounded)) 0 else rounded
}
