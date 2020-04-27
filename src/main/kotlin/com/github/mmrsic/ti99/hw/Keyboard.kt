package com.github.mmrsic.ti99.hw

import com.github.mmrsic.ti99.basic.Breakpoint

/**
 * All codes that may be used by the TI computers.
 */
interface TiCode {
    /** The (ASCII) code used by this [TiCode]. A value between 0 and 255. */
    val code: Int

    /** Convert the [code] of this [TiCode] to a [Char]. */
    fun toChar(): Char = code.toChar()
}

/** All [TiCode]s that may be entered when pressing the CTRL meta key. */
enum class TiCtrlCode(override val code: Int) : TiCode

/** All [TiCode]s that may be entered when pressing the FCTN meta key. */
enum class TiFctnCode(override val code: Int) : TiCode {
    Clear(2),
    Enter(13),
}

/** All [TiCode]s that may be entered when pressing the SHIFT meta key. */
enum class TiShiftCode(override val code: Int) : TiCode {
    A(65), B(66), C(67), D(68), E(69), F(70), G(71), H(72), I(73), J(74), K(75), L(76), M(77),
    N(78), O(79), P(80), Q(81), R(82), S(83), T(84), U(85), V(86), W(87), X(88), Y(89), Z(90)
}

/** All [TiCode]s that may be entered without any meta key. */
enum class TiPlainCode(override val code: Int) : TiCode {
    Digig0(48), Digit1(49), Digit2(50), Digit3(51), Digit4(52),
    Digit5(53), Digit6(54), Digit7(55), Digit8(56), Digit9(57),

    a(97), b(98), c(99), d(100), e(101), f(102), g(103), h(104), i(105), j(105), k(107), l(108), m(109),
    n(110), o(111), p(112), q(113), r(114), s(115), t(116), u(117), v(118), w(119), x(120), y(121), z(122)
}

/** Converter for [TiCode]s. */
object KeyboardConverter {
    /** Mapping from ASCII code to [TiCode]. */
    val map: Map<Int, TiCode>

    init {
        val allKeys = mutableMapOf<Int, TiCode>()
        val keyCodeAdder: (TiCode) -> Unit = { e -> allKeys[e.code] = e }
        TiCtrlCode.values().forEach(keyCodeAdder)
        TiFctnCode.values().forEach(keyCodeAdder)
        TiShiftCode.values().forEach(keyCodeAdder)
        TiPlainCode.values().forEach(keyCodeAdder)
        map = allKeys.toMap()
    }
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
        throw Breakpoint()
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
