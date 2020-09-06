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
enum class TiCtrlCode(override val code: Int, val plainCode: TiPlainCode, comment: String = "") : TiCode {

   SOH(129, TiPlainCode.a, "Start of heading"),
   STX(130, TiPlainCode.b, "Start of text"),
   ETX(131, TiPlainCode.c, "End of text"),
   EOT(132, TiPlainCode.d, "End of transmission"),
   ENQ(133, TiPlainCode.e, "Enquiry"),
   ACK(134, TiPlainCode.f, "Acknowledge"),
   BEL(135, TiPlainCode.g, "Bell"),
   BS(136, TiPlainCode.h, "Backspace"),
   HT(137, TiPlainCode.i, "Horizontal tabulation"),
   LF(138, TiPlainCode.j, "Line feed"),
   VT(139, TiPlainCode.k, "Vertical tabulation"),
   FF(140, TiPlainCode.l, "Form feed"),
   CR(141, TiPlainCode.m, "Carriage return"),
   SO(142, TiPlainCode.n, "Shift out"),
   SI(143, TiPlainCode.o, "Shift in"),
   DLE(144, TiPlainCode.p, "Data link escape"),
   DC1(145, TiPlainCode.q, "Device control 1 (X-ON)"),
   DC2(146, TiPlainCode.r, "Device control 2"),
   DC3(147, TiPlainCode.s, "Device control 3 (X-OFF)"),
   DC4(148, TiPlainCode.t, "Device control 4"),
   NAK(149, TiPlainCode.u, "Negative acknowledge"),
   SYN(150, TiPlainCode.v, "Synchronous idle"),
   ETB(151, TiPlainCode.w, "End of transmission block"),
   CAN(152, TiPlainCode.x, "Cancel"),
   EM(153, TiPlainCode.y, "End of medium"),
   SUB(154, TiPlainCode.z, "Substitute"),
   ESC(155, TiPlainCode.Period, "Escape"),
   FS(156, TiPlainCode.Semicolon, "File separator"),
   GS(157, TiPlainCode.Equals, "Group separator"),
   RS(158, TiPlainCode.Digit8, "Record separator"),
   US(159, TiPlainCode.Digit9, "Unit separator"),

   Ctrl1(177, TiPlainCode.Digit1), Ctrl2(178, TiPlainCode.Digit2), Ctrl3(179, TiPlainCode.Digit3),
   Ctrl4(180, TiPlainCode.Digit4), Ctrl5(181, TiPlainCode.Digit5), Ctrl6(182, TiPlainCode.Digit6),
   Ctrl7(183, TiPlainCode.Digit7), Ctrl0(176, TiPlainCode.Digit0), CtrlSlant(187, TiPlainCode.Slant),
   CtrlComma(128, TiPlainCode.Comma), CtrlPeriod(155, TiPlainCode.Period);

   override fun toString(): String {
      return super.toString() + "(CTRL+$plainCode)"
   }
}

enum class TiFctnShiftCode(override val code: Int, val plainCode: TiPlainCode) : TiCode {
   VerticalBar(124, TiPlainCode.Digit1), LeftBrace(123, TiPlainCode.Digit4), RightBrace(125, TiPlainCode.Digit5),
   Digit0(188, TiPlainCode.Digit0);

   override fun toString(): String {
      return super.toString() + " (FCTN+SHIFT+$plainCode)"
   }
}

/** All [TiCode]s that may be entered when pressing the FCTN meta key. */
enum class TiFctnCode(override val code: Int, val plainCode: TiPlainCode) : TiCode {

   Aid(1, TiPlainCode.Digit7),
   Clear(2, TiPlainCode.Digit4),
   Delete(3, TiPlainCode.Digit1),
   Insert(4, TiPlainCode.Digit2),
   Quit(5, TiPlainCode.Equals),
   Redo(6, TiPlainCode.Digit8),
   Erase(7, TiPlainCode.Digit3),
   Down(10, TiPlainCode.x),
   Proceed(12, TiPlainCode.Digit6),
   Begin(14, TiPlainCode.Digit5),
   Back(15, TiPlainCode.Digit9),

   Tilde(126, TiPlainCode.w), Up(11, TiPlainCode.e), OpenBracket(91, TiPlainCode.r), CloseBracket(93, TiPlainCode.t),
   Line(95, TiPlainCode.u), QuestionMark(63, TiPlainCode.i), Apostrophe(39, TiPlainCode.o), Quote(34, TiPlainCode.p),
   VerticalBar(124, TiPlainCode.a), Left(8, TiPlainCode.s), Right(9, TiPlainCode.d), LeftBrace(123, TiPlainCode.f),
   RightBrace(125, TiPlainCode.g), ReverseSlant(92, TiPlainCode.z), Grave(96, TiPlainCode.c),

   FctnComma(184, TiPlainCode.Comma), FctnPeriod(185, TiPlainCode.Period), FctnB(190, TiPlainCode.b),
   FctnH(191, TiPlainCode.h), FctnJ(192, TiPlainCode.j), FctnK(193, TiPlainCode.k), FctnL(194, TiPlainCode.l),
   FctnM(195, TiPlainCode.m), FctnN(196, TiPlainCode.n), FctnQ(197, TiPlainCode.q), DEL(127, TiPlainCode.v),
   FctnY(198, TiPlainCode.y);

   override fun toString(): String {
      return super.toString() + " (FCTN+$plainCode)"
   }
}

/** All [TiCode]s that may be entered when pressing the SHIFT meta key. */
enum class TiShiftCode(override val code: Int, val plainCode: TiPlainCode) : TiCode {

   ExclamationPoint(33, TiPlainCode.Digit1), AtSign(64, TiPlainCode.Digit2), NumberSign(35, TiPlainCode.Digit3),
   Dollar(36, TiPlainCode.Digit4), Percent(37, TiPlainCode.Digit5), Exponentiation(94, TiPlainCode.Digit6),
   Ampersand(38, TiPlainCode.Digit7), Asterisk(42, TiPlainCode.Digit8), OpenParenthesis(40, TiPlainCode.Digit9),
   CloseParenthesis(41, TiPlainCode.Digit0), Plus(43, TiPlainCode.Equals), Minus(45, TiPlainCode.Slant),
   Colon(58, TiPlainCode.Semicolon), LessThan(60, TiPlainCode.Comma), GreaterThan(62, TiPlainCode.Period),

   A(65, TiPlainCode.a), B(66, TiPlainCode.b), C(67, TiPlainCode.c), D(68, TiPlainCode.d), E(69, TiPlainCode.e),
   F(70, TiPlainCode.f), G(71, TiPlainCode.g), H(72, TiPlainCode.h), I(73, TiPlainCode.i), J(74, TiPlainCode.j),
   K(75, TiPlainCode.k), L(76, TiPlainCode.l), M(77, TiPlainCode.m), N(78, TiPlainCode.n), O(79, TiPlainCode.o),
   P(80, TiPlainCode.p), Q(81, TiPlainCode.q), R(82, TiPlainCode.r), S(83, TiPlainCode.s), T(84, TiPlainCode.t),
   U(85, TiPlainCode.u), V(86, TiPlainCode.v), W(87, TiPlainCode.w), X(88, TiPlainCode.x), Y(89, TiPlainCode.y),
   Z(90, TiPlainCode.z);

   override fun toString(): String {
      return super.toString() + " (SHIFT+$plainCode)"
   }
}

/** All [TiCode]s that may be entered without any meta key. */
@Suppress("EnumEntryName")
enum class TiPlainCode(override val code: Int) : TiCode {

   Digit0(48), Digit1(49), Digit2(50), Digit3(51), Digit4(52),
   Digit5(53), Digit6(54), Digit7(55), Digit8(56), Digit9(57),

   Equals(61), Slant(47), Semicolon(59), Enter(13), Comma(44), Period(46), Space(32),
   a(97), b(98), c(99), d(100), e(101), f(102), g(103), h(104), i(105), j(106), k(107), l(108), m(109),
   n(110), o(111), p(112), q(113), r(114), s(115), t(116), u(117), v(118), w(119), x(120), y(121), z(122)
}

/** Get the TI 99/4a key code for a given (ASCII) code. */
fun ti994aKeyForCode(wantedCode: Int): TiCode {
   for (plainKey in TiPlainCode.values()) {
      if (plainKey.code == wantedCode) return plainKey
   }
   for (shiftKey in TiShiftCode.values()) {
      if (shiftKey.code == wantedCode) return shiftKey
   }
   for (ctrlKey in TiCtrlCode.values()) {
      if (ctrlKey.code == wantedCode) return ctrlKey
   }
   for (fctnKey in TiFctnCode.values()) {
      if (fctnKey.code == wantedCode) return fctnKey
   }
   for (fctnShiftKey in TiFctnShiftCode.values()) {
      if (fctnShiftKey.code == wantedCode) return fctnShiftKey
   }
   throw IllegalArgumentException("No TI key code found for code=$wantedCode")
}

/** Get the code of the CALL KEY command for a given key unit >= 0 and a given [TiCode]. */
fun ti994aCodeForKeyUnitKey(keyUnit: Int, code: TiCode): Int {
   return when (keyUnit) {
      0 -> code.code
      1 -> when (code) {
         TiPlainCode.x, TiShiftCode.X -> 0
         TiPlainCode.a, TiShiftCode.A -> 1
         TiPlainCode.s, TiShiftCode.S -> 2
         TiPlainCode.d, TiShiftCode.D -> 3
         TiPlainCode.w, TiShiftCode.W -> 4
         TiPlainCode.e, TiShiftCode.E -> 5
         TiPlainCode.r, TiShiftCode.R -> 6
         TiPlainCode.Digit2, TiShiftCode.AtSign -> 7
         TiPlainCode.Digit3, TiShiftCode.NumberSign -> 8
         TiPlainCode.Digit4, TiShiftCode.Dollar -> 9
         TiPlainCode.Digit5, TiShiftCode.Percent -> 10
         TiPlainCode.t, TiShiftCode.T -> 11
         TiPlainCode.f, TiShiftCode.F -> 12
         TiPlainCode.v, TiShiftCode.V -> 13
         TiPlainCode.c, TiShiftCode.C -> 14
         TiPlainCode.z, TiShiftCode.Z -> 15
         TiPlainCode.b, TiShiftCode.B -> 16
         TiPlainCode.g, TiShiftCode.G -> 17
         TiPlainCode.q, TiShiftCode.Q -> 18
         TiPlainCode.Digit1, TiShiftCode.ExclamationPoint -> 19
         else -> -1
      }
      2 -> when (code) {
         TiPlainCode.m, TiShiftCode.M -> 0
         TiPlainCode.h, TiShiftCode.H -> 1
         TiPlainCode.j, TiShiftCode.J -> 2
         TiPlainCode.k, TiShiftCode.K -> 3
         TiPlainCode.u, TiShiftCode.U -> 4
         TiPlainCode.i, TiShiftCode.I -> 5
         TiPlainCode.o, TiShiftCode.O -> 6
         TiPlainCode.Digit7, TiShiftCode.Ampersand -> 7
         TiPlainCode.Digit8, TiShiftCode.Asterisk -> 8
         TiPlainCode.Digit9, TiShiftCode.OpenParenthesis -> 9
         TiPlainCode.Digit0, TiShiftCode.CloseParenthesis -> 10
         TiPlainCode.p, TiShiftCode.P -> 11
         TiPlainCode.l, TiShiftCode.L -> 12
         TiPlainCode.Period, TiShiftCode.GreaterThan -> 13
         TiPlainCode.Comma, TiShiftCode.LessThan -> 14
         TiPlainCode.n, TiShiftCode.N -> 15
         TiPlainCode.Slant, TiShiftCode.Minus -> 16
         TiPlainCode.Semicolon, TiShiftCode.Colon -> 17
         TiPlainCode.y, TiShiftCode.Y -> 18
         TiPlainCode.Digit6, TiShiftCode.Exponentiation -> 19
         else -> -1
      }
      else -> error("Don't know ASCII code for key unit $keyUnit and code $code")
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
