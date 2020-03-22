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
enum class TiCtrlCode(override val code: Int) : TiCode

/**
 * All codes that may be entered when pressing the FCTN meta key.
 */
enum class TiFctnCode(override val code: Int) : TiCode {
    Clear(2),
    Enter(13),
}

/**
 * All codes that may be entered when pressing the SHIFT meta key.
 */
enum class TiShiftCode(override val code: Int) : TiCode {
    A(65), B(66), C(67), D(68), E(69), F(70), G(71), H(72), I(73)
}

/**
 * A provider of [Char] sequences which are interpreted as [TiCode]s.
 */
interface KeyboardInputProvider {

    /** All TI key codes currently pressed on the keyboard.  */
    fun currentlyPressedKeyCode(ctx: CallKeyContext): TiCode? {
        return null
    }

    /** Context used when [currentlyPressedKeyCode] is called. */
    interface CallKeyContext {
        /** The key unit 1-5 for the TI 99/4a keyboard. */
        val keyUnit: Int

        /** The program line number of the CALL KEY causing the call, null if a command, not null if a statement. */
        val programLineNumber: Int?
    }

    /**
     * Provide code sequence input as a [Sequence] of [TiCode]s. If the return value contains a character with code 13,
     * the input simulation ends. Otherwise, this method is called again.
     *
     * Throw Breakpoint() in order to leave the program while the input command is active.
     *
     * @param ctx [InputContext] used to distinguish programmatically generated sequences
     * @return a sequence of characters typed in by the user - must not be null but may be empty
     */
    fun provideInput(ctx: InputContext): Sequence<Char> {
        return sequenceOf(TiFctnCode.Enter.toChar())
    }

    /** Context passed to any call of [provideInput]. */
    interface InputContext {
        /** The prompt presented to the user. */
        val prompt: String

        /** Number of overall calls of any [KeyboardInputProvider] within a program run. Starts at 1. */
        val overallCalls: Int

        /** Program line currently requesting the input. Ranges from 1 to 32767. */
        val programLine: Int

        /** Number of calls made from the [programLine] within a program run. */
        val programLineCalls: Int

        /** Number of unaccepted inputs at the current [programLine] and the current [programLineCalls]. */
        val unacceptedInputs: Int
    }
}
