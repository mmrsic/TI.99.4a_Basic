package com.github.mmrsic.ti99.basic

import com.github.mmrsic.ti99.basic.expr.NumericConstant
import com.github.mmrsic.ti99.basic.expr.StringConstant
import com.github.mmrsic.ti99.basic.expr.quote
import com.github.mmrsic.ti99.basic.expr.unquote
import java.lang.Integer.max
import java.lang.Integer.min
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import kotlin.math.roundToInt

/** TI Basic encoding stuff. */
object TiBasicEncoding {

   /** Standard TI Basic encoding character set. */
   val CHARSET: Charset = StandardCharsets.US_ASCII
}

/** A class which is able to encode numeric and string constants into a byte array. */
enum class Encoder {

   /** TI Basic DISPLAY [Encoder]. */
   DISPLAY {

      override fun encode(numericConstant: NumericConstant): ByteArray {
         return numericConstant.displayValue().toByteArray(TiBasicEncoding.CHARSET)
      }

      override fun encode(stringConstant: StringConstant): ByteArray {
         val originalString = stringConstant.constant
         return originalString.toByteArray(TiBasicEncoding.CHARSET)
      }
   },

   /** Ti Basic INTERNAL [Encoder]. */
   INTERNAL {

      override fun encode(numericConstant: NumericConstant): ByteArray {
         val zeroExponent: Byte = 0x40
         val numberValue = numericConstant.value().toNative()
         val numberString = if (numericConstant.isInteger) {
            numberValue.toInt().toString()
         } else {
            "%.${14}f".format(numberValue).replace(Regex(",0*"), "")
         }
         val numDigitsBeforeDot = numberValue.toInt().toString().length
         val exponentOffset = (numDigitsBeforeDot / 2.0).roundToInt() - 1
         val exponent = zeroExponent + exponentOffset
         val digitBytes = ByteArray(8)
         digitBytes[0] = exponent.toByte()
         val x = numDigitsBeforeDot % 2
         for (i in 1 until digitBytes.size) {
            val startIndex = max(0, (i - 1) * 2 - x)
            val endIndex = min(numberString.length, i * 2 - x)
            val twoDigitString = when {
               startIndex < numberString.length -> numberString.substring(startIndex, endIndex)
               else -> "0"
            }
            digitBytes[i] = (if (i > 1) twoDigitString.padEnd(2, '0') else twoDigitString).toByte()
         }
         val precededDigitBytes = digitBytes.copyInto(ByteArray(digitBytes.size + 1), 1)
         precededDigitBytes[0] = digitBytes.size.toByte()
         return precededDigitBytes
      }

      override fun encode(stringConstant: StringConstant): ByteArray {
         val nativeString = stringConstant.toNative()
         return ByteArray(nativeString.length + 1) { byteOffset ->
            when (byteOffset) {
               0 -> nativeString.length.toByte()
               in 1..nativeString.length -> nativeString[byteOffset - 1].toByte()
               else -> error("Illegal string byte representation offset: $byteOffset")
            }
         }
      }
   };

   /** Encode a given numeric constant into the representation of this [Encoder]. */
   abstract fun encode(numericConstant: NumericConstant): ByteArray

   /** Encode a given string constant into the representation of this [Encoder]. */
   abstract fun encode(stringConstant: StringConstant): ByteArray
}

/** A class which is able to decode numeric and string constants from a byte array. */
enum class Decoder {

   /** [Decoder] for INTERNAL representation of constants. */
   INTERNAL {

      override fun decodeNumber(bytes: ByteArray): NumericConstant {
         TODO("not implemented")
      }

      override fun decodeString(bytes: ByteArray): StringConstant {
         return StringConstant(bytes.toString(TiBasicEncoding.CHARSET).substring(1))
      }
   },

   /** [Decoder] for DISPLAY representation of constants. */
   DISPLAY {

      override fun decodeNumber(bytes: ByteArray): NumericConstant {
         return NumericConstant(bytes.toString(TiBasicEncoding.CHARSET).toDouble())
      }

      override fun decodeString(bytes: ByteArray): StringConstant {
         return StringConstant(unquote(bytes.toString(TiBasicEncoding.CHARSET))!!)
      }
   };

   /** Decode a given byte array as a numeric constant. */
   abstract fun decodeNumber(bytes: ByteArray): NumericConstant

   /** Decode a given byte array as a string constant. */
   abstract fun decodeString(bytes: ByteArray): StringConstant
}