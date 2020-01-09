package com.github.mmrsic.ti99.basic.expr

interface Expression {
    fun value(): Constant
    fun displayValue(): String
}

interface Constant {
    fun toNative(): Any
}