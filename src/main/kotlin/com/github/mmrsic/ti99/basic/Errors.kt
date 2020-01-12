package com.github.mmrsic.ti99.basic

sealed class TiBasicException(msg: String) : Exception(msg)
sealed class TiBasicError(msg: String) : TiBasicException(msg)
sealed class TiBasicWarning(msg: String) : TiBasicException(msg)

class BadLineNumber : TiBasicError("BAD LINE NUMBER")
class BadName : TiBasicError("BAD NAME")
class BadValue : TiBasicError("BAD VALUE")
class CantDoThat : TiBasicError("CAN'T DO THAT")
class IncorrectStatement : TiBasicError("INCORRECT STATEMENT")
class NumberTooBig(val replacementValue: Number) : TiBasicWarning("NUMBER TOO BIG")
class StringNumberMismatch() : TiBasicError("STRING-NUMBER MISMATCH")
