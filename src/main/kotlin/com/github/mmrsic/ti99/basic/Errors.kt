package com.github.mmrsic.ti99.basic

sealed class TiBasicException(msg: String) : Exception(msg)
sealed class TiBasicError(msg: String) : TiBasicException(msg)
sealed class TiBasicWarning(msg: String) : TiBasicException(msg)

open class TiBasicProgramException(val lineNumber: Int, val delegate: TiBasicException? = null) : TiBasicException("") {
    override val message: String
        get() {
            val msg = if (delegate?.message != null) delegate.message!! else "Execution exception"
            val lineNumPrefix = if (delegate is Breakpoint) "AT" else "IN"
            return "$msg $lineNumPrefix $lineNumber"
        }
}


class BadLineNumber : TiBasicError("BAD LINE NUMBER")
class BadName : TiBasicError("BAD NAME")
class BadValue : TiBasicError("BAD VALUE")
class Breakpoint : TiBasicError("BREAKPOINT")
class CantContinue : TiBasicError("CAN'T CONTINUE")
class CantDoThat : TiBasicError("CAN'T DO THAT")
class IncorrectStatement : TiBasicError("INCORRECT STATEMENT")
class NumberTooBig : TiBasicWarning("NUMBER TOO BIG")
class StringNumberMismatch() : TiBasicError("STRING-NUMBER MISMATCH")
