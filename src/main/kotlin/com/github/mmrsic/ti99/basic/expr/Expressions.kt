package com.github.mmrsic.ti99.basic.expr

/**
 * An expression used in a command or statement of TI Basic.
 * It has a value, a display value and a list text.
 */
interface Expression {
    /** Value of this expression as a [Constant]. */
    fun value(): Constant

    /** [value] of this expression as printed on the TI Basic screen. */
    fun displayValue(): String

    /** String representing this expression as output by LIST command. */
    fun listText(): String
}

/**
 * A constant may be converted to a native value, that is, a Kotlin value.
 */
interface Constant {
    fun toNative(): Any
}