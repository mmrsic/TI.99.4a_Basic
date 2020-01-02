package com.github.mmrsic.ti99.basic.expr

abstract class Expression {
    abstract fun calculate(): Any
    abstract fun displayValue(): String
}