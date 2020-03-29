package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.math.PI
import kotlin.test.assertEquals

class CosFunctionTest {

    @Test
    fun testSomeRadian() {
        val argument = 1.047197551196
        val result = CosFunction(NumericConstant(argument)).value()
        assertEquals(" .5 ", result.displayValue(), "COS($argument) must yield .5")
    }

    @Test
    fun testSixtyDegreesInsteadOfRadians() {
        val argument = 60
        val result = CosFunction(NumericConstant(argument)).value()
        assertEquals("-.9524129804 ", result.displayValue(), "COS($argument) must yield -.9524129804")
    }

    @Test
    fun testSixtyDegreesAsRadians() {
        val pi = NumericConstant(PI)
        val sixty = NumericConstant(60)
        val oneHundredEighty = NumericConstant(180)
        val argument = Division(Multiplication(sixty, pi), oneHundredEighty)
        val result = CosFunction(argument.value()).value()
        assertEquals(" .5 ", result.displayValue(), "COS($argument) must yield .5")
    }

    @Test
    fun test() {
        val alpha = NumericConstant(.7635) // radians
        val beta = NumericConstant(-8.348) // radians
        assertEquals(" .722420351 ", CosFunction(alpha).value().displayValue())
        assertEquals("-.4741676154 ", CosFunction(beta).value().displayValue())
    }
}