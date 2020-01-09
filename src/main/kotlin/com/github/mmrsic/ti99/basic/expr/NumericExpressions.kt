package com.github.mmrsic.ti99.basic.expr

import com.github.mmrsic.ti99.basic.NumberTooBig
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.math.*

object NumberRanges {
    internal const val MIN_VALUE = 1e-128
    internal const val MAX_VALUE = 9.9999999999999e127

    fun isOverflow(value: Number): Boolean {
        return abs(value.toDouble()) > MAX_VALUE
    }

    fun isUnderflow(value: Number): Boolean {
        return value != 0 && abs(value.toDouble()) < MIN_VALUE
    }
}

abstract class NumericExpr : Expression {
    abstract override fun value(): NumericConstant
    override fun displayValue(): String = toTiDisplayNumber(value().toNative())
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
    override fun value() = NumericConstant(op1.value().toNative().pow(op2.value().toNative()))
}

data class NegatedExpression(val original: NumericExpr) : NumericExpr() {
    override fun value() = NumericConstant(-original.value().toNative())
}

data class NumericConstant(private val constant: Number) : NumericExpr(), Constant {
    val isOverflow = NumberRanges.isOverflow(constant)

    override fun value(): NumericConstant = this

    /** Always the native, that is, the [Double] value of this numeric constant. */
    override fun toNative(): Double =
        if (isOverflow) sign(constant.toDouble()) * NumberRanges.MAX_VALUE else toTiNumber(constant).toDouble()

    override fun displayValue(): String {
        if (!isOverflow) {
            return super.displayValue()
        }
        return (if (constant.toDouble() < 0) "-" else " ") + "9.99999E+**"
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

private fun toTiDisplayNumber(original: Double): String {
    if (original.toString().contains('E')) {
        if (NumberRanges.isUnderflow(original)) {
            return " 0 "
        } else if (NumberRanges.isOverflow(original)) {
            val sign = if (original < 0) "-" else " "
            return sign + "9.99999E+**"
        }

        val positiveSign = if (original < 0) "" else " "
        val bigDecimal = BigDecimal(original)
        val scaledDecimal = bigDecimal.setScale(-(bigDecimal.precision() - 9), RoundingMode.HALF_UP)
        return positiveSign + scaledDecimal.toString().replace(Regex("(0+E)"), "E")
    }

    val text = if (original.roundToInt().toDouble() == original) {
        original.toInt().toString()
    } else {
        val numDigitsBeforeDot = if (original == 0.0) 0 else log10(abs(original)).toInt()
        val numDigits = 9 - numDigitsBeforeDot + (if (abs(original) < 1) 1 else 0)
        val formatSpec = "%." + numDigits + "f"
        formatSpec.format(Locale.US, original).trimEnd('0').replace(Regex("^0+"), "")
    }
    return if (original >= 0) " $text " else "$text "
}