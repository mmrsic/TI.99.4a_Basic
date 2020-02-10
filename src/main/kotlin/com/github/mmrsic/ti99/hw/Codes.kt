package com.github.mmrsic.ti99.hw

interface TiCode {
    val code: Int
    fun toChar(): Char = code.toChar()
}

enum class TiCtrlCode(override val code: Int) : TiCode {
}

enum class TiFctnCode(override val code: Int) : TiCode {
    Clear(2),
    Enter(13),
}


interface CodeSequenceProvider {

    /**
     * Provide code sequence input as a [Sequence] of [TiCode]s.
     * @param ctx [Context] used to distinguish programmatically generated sequences
     */
    fun provideInput(ctx: Context): Sequence<Char> {
        return sequenceOf(TiFctnCode.Enter.toChar())
    }

    /** Context passed to any call of [provideInput]. */
    interface Context {
        /** Number of overall calls of any [CodeSequenceProvider] within a program run. Starts at 1. */
        val overallCalls: Int
        /** Program line currently requesting the input. Ranges from 1 to 32767. */
        val programLine: Int
        /** Number of calls made from the [programLine] within a program run. */
        val programLineCalls: Int
        /** Number of unaccepted inputs at the current [programLine] and the current [programLineCalls]. */
        val unacceptedInputs: Int
    }
}
