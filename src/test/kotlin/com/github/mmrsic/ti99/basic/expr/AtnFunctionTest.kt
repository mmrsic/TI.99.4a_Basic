package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.test.assertEquals

class AtnFunctionTest {

    @Test
    fun testZero() {
        val func = AtnFunction(NumericConstant(0))
        assertEquals(0, func.calculateToConstant().value(), "ATN(0) must yield 0")
    }

    @Test
    fun testPointFourFour() {
        val func = AtnFunction(NumericConstant(.44))
        assertEquals(.4145068746, func.calculateToConstant().value(), "ATN(.44) must yield .4145068746")
    }

}