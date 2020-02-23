package com.github.mmrsic.ti99.basic

import com.github.mmrsic.ti99.hw.TiBasicScreen

/** [Exception]s while executing TI Basic commands or statements. */
sealed class TiBasicException(msg: String) : Exception(msg) {
    /** Check whether this exception represents a warning only, and a program may proceed. */
    open val isWarning: Boolean = false
    /** Optional line number of this exception. */
    open val lineNumber: Int? = null
    /** Text used when optional [lineNumber] is displayed. */
    open val lineNumberPrefix: String = "IN"
    /** Whether [displayOn] will scroll the screen's contents after displaying this exception. */
    open val scrollAfterDisplay: Boolean = true

    /** Display this exception on a given [TiBasicScreen].*/
    fun displayOn(screen: TiBasicScreen) {
        screen.scroll()
        val excText = "$message${if (lineNumber != null) " $lineNumberPrefix $lineNumber" else ""}"
        if (isWarning) {
            screen.print("* WARNING:")
            screen.print("  $excText")
        } else {
            screen.print("* $excText")
        }
        if (scrollAfterDisplay) screen.scroll()
    }
}

/** [TiBasicException] representing errors which abort execution. */
sealed class TiBasicError(msg: String) : TiBasicException(msg)

/** [TiBasicException] representing warnings which don't abort execution. */
sealed class TiBasicWarning(msg: String) : TiBasicException(msg) {
    override val isWarning = true
}

/** A [TiBasicException] occurring at a line number within a program. */
open class TiBasicProgramException(override val lineNumber: Int, val delegate: TiBasicException? = null) :
    TiBasicException("") {
    override val message = delegate?.message ?: super.message
    override val isWarning = delegate?.isWarning ?: super.isWarning
    override val lineNumberPrefix = delegate?.lineNumberPrefix ?: super.lineNumberPrefix
    override val scrollAfterDisplay = delegate?.scrollAfterDisplay ?: super.scrollAfterDisplay
}

class BadLineNumber : TiBasicError("BAD LINE NUMBER")
class BadLineNumberWarning : TiBasicWarning("BAD LINE NUMBER") {
    override val scrollAfterDisplay = false
}

class BadName : TiBasicError("BAD NAME")
class BadValue : TiBasicError("BAD VALUE")
class Breakpoint : TiBasicError("BREAKPOINT") {
    override val lineNumberPrefix = "AT"
    override val scrollAfterDisplay = false
}

class CantContinue : TiBasicError("CAN'T CONTINUE")
class CantDoThat : TiBasicError("CAN'T DO THAT")
class DataError : TiBasicError("DATA ERROR")
class IncorrectStatement : TiBasicError("INCORRECT STATEMENT")
class InputError : TiBasicWarning("INPUT ERROR") {
    override val scrollAfterDisplay = false
}

class NumberTooBig : TiBasicWarning("NUMBER TOO BIG") {
    override val scrollAfterDisplay = false
}
