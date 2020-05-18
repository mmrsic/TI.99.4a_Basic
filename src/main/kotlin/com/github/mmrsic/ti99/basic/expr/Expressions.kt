package com.github.mmrsic.ti99.basic.expr

/**
 * An expression used in a command or statement of TI Basic.
 * It has a value, a display value and a list text.
 */
interface Expression {

   /** Value of this expression as a [Constant]. */
   fun value(lambda: (value: Constant) -> Any = {}): Constant {
      return value()
   }

   /** [value] of this expression as printed on the TI Basic screen. */
   fun displayValue(): String

   /** String representing this expression as output by LIST command. */
   fun listText(): String
}

/** A constant which may be printed to a device. */
interface PrintConstant : Expression

/**
 * A constant may be converted to a native value, that is, a Kotlin value.
 */
interface Constant : PrintConstant {

   /** The original constant of this instance. */
   val constant: Any

   /** The native value of this constant that is used within arithmetic operations. */
   fun toNative(): Any
}

/** Separators used for formatting in PRINT statement. */
enum class PrintSeparator : PrintConstant {

   /** Print next value adjacent to previous one. */
   Adjacent,

   /** Print next value at the next field. For printing on the screen, this means the next (of two) columns. */
   NextField,

   /** Print next vale at the next record. For printing on the screen, this means the next row. */
   NextRecord;

   override fun displayValue() = when (this) {
      Adjacent -> ";"
      NextField -> ","
      NextRecord -> ":"
   }

   override fun listText() = displayValue()
   override fun value(lambda: (value: Constant) -> Any): Constant = StringConstant(displayValue())

   companion object {
      /**
       * The [PrintSeparator] for a given text representation.
       * @param text text representation of the print token as given by [value]
       * @return any of the [PrintSeparator.values] if its [value] equals the specified text, null if no such print token
       * exists
       */
      fun fromString(text: String): PrintSeparator? = when (text) {
         ";" -> Adjacent
         "," -> NextField
         ":" -> NextRecord
         else -> null
      }
   }
}
