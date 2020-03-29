package com.github.mmrsic.ti99.basic

/**
 * Generator of pseudo-random values using a linear congruential generator (LCG).
 */
class PseudoRandomGenerator(val factor: Int, val summand: Int) {

    /** The seed used in [nextRandom]. */
    object Seed {
        var value: Int = 0
    }

    /** Generate the next random value according to the [Seed] and a given [divisor]. */
    fun nextRandom(divisor: Int): Int {
        Seed.value = ((Seed.value * factor).and(0xffff)) + summand
        return ((Seed.value.and(0xff00).shr(8)).or(Seed.value.and(0x00ff).shl(8))) % (if (divisor == 0) 1 else divisor)
    }

}

