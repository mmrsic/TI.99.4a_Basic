package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.test.assertEquals

class AtnFunctionTest {

    @Test
    fun testZero() {
        val result = AtnFunction(NumericConstant(0)).value()
        assertEquals(0.0, result.toNative(), "ATN(0) must yield 0")
        assertEquals(" 0 ", result.displayValue(), "ATN(0) must yield 0")
    }

    @Test
    fun testPointFourFour() {
        val result = AtnFunction(NumericConstant(.44)).value()
        assertEquals(0.4145068746, result.toNative(), "ATN(.44) must yield .4145068746")
        assertEquals(" .4145068746 ", result.displayValue(), "ATN(.44) must yield .4145068746")
    }

}