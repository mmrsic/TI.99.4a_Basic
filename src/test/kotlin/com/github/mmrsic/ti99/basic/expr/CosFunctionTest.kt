package com.github.mmrsic.ti99.basic.expr

import org.junit.Test
import kotlin.math.PI
import kotlin.test.assertEquals

class CosFunctionTest {

    @Test
    fun testSomeRadian() {
        val argument = 1.047197551196
        val func = CosFunction(NumericConstant(argument))
        assertEquals(.5, func.calculateToConstant().value(), "COS($argument) must yield .5")
    }

    @Test
    fun testSixtyDegreesInsteadOfRadians() {
        val argument = 60
        val func = CosFunction(NumericConstant(argument))
        assertEquals(-.9524129804, func.calculateToConstant().value(), "COS($argument) must yield -.9524129804")
    }

    @Test
    fun testSixtyDegreesAsRadians() {
        val pi = NumericConstant(PI)
        val sixty = NumericConstant(60)
        val oneHundredEighty = NumericConstant(180)
        val argument = Division(Multiplication(sixty, pi), oneHundredEighty)
        val func = CosFunction(NumericConstant(argument.calculate()))
        assertEquals(.5, func.calculateToConstant().value(), "COS($argument) must yield .5")
    }

    @Test
    fun testFortyFiveDegreesAsRadians() {
        val converter = Division(AtnFunction(NumericConstant(1)), NumericConstant(45))
        val fortyFive = NumericConstant(45)
        val argument = Multiplication(fortyFive, converter)
        val func = CosFunction(NumericConstant(argument.calculate()))
        assertEquals(.7071067812, func.calculateToConstant().value(), "COS($argument) must yield .7071067812")
    }

    @Test
    fun test() {
        val alpha = NumericConstant(.7635) // radians
        val beta = NumericConstant(-8.348) // radians
        val gamma = NumericConstant(65.39) // degrees
        assertEquals(.722420351, CosFunction(alpha).calculateToConstant().value())
        assertEquals(-.4741676154, CosFunction(beta).calculateToConstant().value())
        assertEquals(
            .4164394776,
            CosFunction(
                Multiplication(
                    gamma,
                    Division(AtnFunction(NumericConstant(1)), NumericConstant(45))
                )
            ).calculateToConstant().value()
        )
    }
}