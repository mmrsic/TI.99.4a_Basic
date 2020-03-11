package com.github.mmrsic.ti99.hw

/**
 * All codes that may be used by the TI computers.
 */
interface TiCode {
    val code: Int
    fun toChar(): Char = code.toChar()
}

/**
 * All codes that may be entered when pressing the CTRL meta key.
 */
enum class TiCtrlCode(override val code: Int) : TiCode {
}

/**
 * All codes that may be entered when pressing the FCTN meta key.
 */
enum class TiFctnCode(override val code: Int) : TiCode {
    Clear(2),
    Enter(13),
}

/**
 * A provider of [Char] sequences which are interpreted as [TiCode]s.
 */
interface CodeSequenceProvider {

    /**
     * Provide code sequence input as a [Sequence] of [TiCode]s. If the return value contains a character with code 13,
     * the input simulation ends. Otherwise, this method is called again.
     *
     * Throw Breakpoint() in order to leave the program while the input command is active.
     *
     * @param ctx [Context] used to distinguish programmatically generated sequences
     * @return a sequence of characters typed in by the user - must not be null but may be empty
     */
    fun provideInput(ctx: Context): Sequence<Char> {
        return sequenceOf(TiFctnCode.Enter.toChar())
    }

    /** Context passed to any call of [provideInput]. */
    interface Context {
        /** The prompt presented to the user. */
        val prompt: String

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
