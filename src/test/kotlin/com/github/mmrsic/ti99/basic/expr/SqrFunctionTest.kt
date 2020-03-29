package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.test.assertEquals

class SqrFunctionTest {

    @Test
    fun testFour() {
        val argument = NumericConstant(4)
        val result = SqrFunction(argument).value()
        assertEquals(2.0, result.toNative(), "SQR($argument)")
        assertEquals(" 2 ", result.displayValue(), "SQR($argument)")
    }

    @Test
    fun testTwoPointFiveSevenE5() {
        val argument = NumericConstant(2.57e5)
        val result = SqrFunction(argument).value()
        assertEquals(" 506.9516742 ", result.displayValue(), "SQR($argument)")
    }

}