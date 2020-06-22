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

   /** Whether [displayOn] will scroll the screen's contents before displaying this exception. */
   open val scrollBeforeDisplay: Boolean = true

   /** Whether [displayOn] will scroll the screen's contents after displaying this exception. */
   open val scrollAfterDisplay: Boolean = true

   /** Display this exception on a given [TiBasicScreen].*/
   fun displayOn(screen: TiBasicScreen) {
      if (scrollBeforeDisplay) screen.scroll()
      val excText = "$message${if (lineNumber != null) " $lineNumberPrefix $lineNumber" else ""}"
      if (isWarning) {
         screen.print("* WARNING:")
         screen.print("  $excText")
      } else if (excText.length <= 25 || lineNumber == null) {
         screen.print("* $excText")
      } else {
         screen.print("* $message")
         screen.print("   $lineNumberPrefix $lineNumber")
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

class BadArgument : TiBasicError("BAD ARGUMENT")
class BadName : TiBasicError("BAD NAME") {
   override val scrollBeforeDisplay = false
}

class BadValue : TiBasicError("BAD VALUE")
class BadSubscript : TiBasicError("BAD SUBSCRIPT")
class Breakpoint : TiBasicError("BREAKPOINT") {
   override val lineNumberPrefix = "AT"
   override val scrollAfterDisplay = false
}

class CantContinue : TiBasicError("CAN'T CONTINUE") {
   override val scrollBeforeDisplay = false
}

class CantDoThat : TiBasicError("CAN'T DO THAT")
class DataError : TiBasicError("DATA ERROR")
class FileError : TiBasicError("FILE ERROR")
class ForNextError : TiBasicError("FOR-NEXT ERROR")
class IncorrectStatement : TiBasicError("INCORRECT STATEMENT") {
   override val scrollBeforeDisplay = false
}

class InOutError(val number: String) : TiBasicError("I/O ERROR") {
   override val message = super.message + " $number"
}

class InputError : TiBasicError("INPUT ERROR")
class InputWarning : TiBasicWarning("INPUT ERROR") {
   override val scrollAfterDisplay = false
}

class MemoryFull : TiBasicError("MEMORY FULL")
class NameConflict : TiBasicError("NAME CONFLICT")
class NumberTooBig : TiBasicWarning("NUMBER TOO BIG") {
   override val scrollAfterDisplay = false
}
