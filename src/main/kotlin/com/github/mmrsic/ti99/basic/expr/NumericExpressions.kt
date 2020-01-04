package com.github.mmrsic.ti99.basic.expr

import java.util.*
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

object NumberRanges {
    private const val MIN_VALUE = 1e-128
    private const val MAX_VALUE = 9.9999999999999e127

    /** Whether a given number is in the TI Basic's number range. */
    fun isInRange(value: Number): Boolean {
        val absValue = abs(value.toDouble())
        return absValue in MIN_VALUE..MAX_VALUE
    }

    fun isOverflow(value: Number): Boolean {
        return abs(value.toDouble()) > MAX_VALUE
    }

    fun isUnderflow(value: Number): Boolean {
        return value != 0 && abs(value.toDouble()) < MIN_VALUE
    }
}

abstract class NumericExpr : Expression() {
    abstract override fun calculate(): Number
    override fun displayValue(): String {
        val num = toTiNumber(calculate())
        return toTiDisplayNumber(num)
    }

    open fun calculateToConstant(): NumericConstant = NumericConstant(calculate())
}

data class Addition(val op1: NumericExpr, val op2: NumericExpr) : NumericExpr() {
    override fun calculate(): Number = op1.calculate().toDouble() + op2.calculate().toDouble()
}

data class Subtraction(val op1: NumericExpr, val op2: NumericExpr) : NumericExpr() {
    override fun calculate(): Number = op1.calculate().toDouble() - op2.calculate().toDouble()
}

data class Multiplication(val op1: NumericExpr, val op2: NumericExpr) : NumericExpr() {
    override fun calculate(): Number = op1.calculate().toDouble() * op2.calculate().toDouble()
}

data class Division(val op1: NumericExpr, val op2: NumericExpr) : NumericExpr() {
    override fun calculate(): Number = op1.calculate().toDouble() / op2.calculate().toDouble()
}

data class Exponentiation(val op1: NumericExpr, val op2: NumericExpr) : NumericExpr() {
    override fun calculate(): Number = op1.calculate().toDouble().pow(op2.calculate().toDouble())
}


data class NumericConstant(private val constant: Number) : NumericExpr() {
    override fun calculate(): Number = constant
    override fun calculateToConstant() = this

    fun value() = toTiNumber(constant)
}

data class NumericVariable(val name: String, val calc: (String) -> Number) : NumericExpr() {
    override fun calculate(): Number = calc.invoke(name)
}


// HELPERS //

private fun toTiNumber(original: Number): Number {
    if (original is Int) {
        return original.toDouble()
    }
    val rounded = "%.10f".format(Locale.US, original).toDouble()
    val asInt = original.toInt()
    if (asInt.compareTo(rounded) == 0) {
        return asInt
    }
    return rounded
}

private fun toTiDisplayNumber(original: Number): String {
    val originalDouble = original.toDouble()
    if (original.toString().contains('E')) {
        if (NumberRanges.isUnderflow(original)) {
            return " 0 "
        } else if (NumberRanges.isOverflow(original)) {
            return (if (originalDouble < 0) "-" else " ") + "9.99999E+**"
        }
        return original.toString().replace("E", "E+")
    }

    val text = if (originalDouble.roundToInt().toDouble() == originalDouble) {
        original.toInt().toString()
    } else {
        val numDigitsBeforeDot = if (original == 0) 0 else log10(abs(originalDouble)).toInt()
        val formatSpec = "%." + (9 - numDigitsBeforeDot) + "f"
        formatSpec.format(Locale.US, original).trimEnd('0')
    }
    return if (originalDouble >= 0) " $text " else "$text "
}