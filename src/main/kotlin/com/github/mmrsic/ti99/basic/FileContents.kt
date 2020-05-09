package com.github.mmrsic.ti99.basic

import com.github.mmrsic.ti99.basic.expr.Constant
import com.github.mmrsic.ti99.basic.expr.NumericConstant

/** Instance which is able to recognize a TI file contents type. */
interface TiFileContentsRecognizer {

   /**Whether this instance recognizes a given byte array as of matching its type. */
   fun isRecognized(bytes: ByteArray): Boolean

   /** The meta data describing the file type, or null if the specified byte array is not recognized by this instance. */
   fun getMetaData(bytes: ByteArray): TiFileContentMetaData?

   /* The contents of specified byte array without any [getMetaData]. **/
   fun getContents(bytes: ByteArray): TiFileContents?
}

/** Representation of a TI file contents. */
interface TiFileContents {

   val length: Int

   /** All the bytes of this [TiFileContents]. */
   fun getBytes(): ByteArray

   /** An iterator over [getBytes]. */
   fun iterateBytes(): ByteIterator
   fun readByte(offset: Int): Byte
   fun readBytes(startOffset: Int, resultLength: Int): ByteArray
}

/** The meta data wrapping or describing a [TiFileContents]. */
interface TiFileContentMetaData {

   fun getBytes(): ByteArray
}

/** A [TiFileContentsRecognizer] for TIFILES files. */
object TiFileContentRecognizerTiFiles : TiFileContentsRecognizer {

   /** Bytes at the very start of each TIFILES file. */
   private val headerStart by lazy {
      (7.toChar() + "TIFILES").toByteArray(Charsets.US_ASCII)
   }

   /** Number of bytes each TIFILES header contains. */
   const val headerLength = 128

   override fun isRecognized(bytes: ByteArray): Boolean {
      return headerStart.indices.all { idx -> bytes.size > idx && headerStart[idx] == bytes[idx] }
   }

   override fun getMetaData(bytes: ByteArray): TiFileContentMetaData? =
      if (isRecognized(bytes)) TiFileContentMetaDataTiFiles(bytes.copyOfRange(0, headerLength)) else null

   override fun getContents(bytes: ByteArray): TiFileContents? =
      if (isRecognized(bytes)) TiFileContentsBytes(bytes.copyOfRange(0, headerLength)) else null
}

class TiFileContentMetaDataTiFiles(contents: ByteArray) : TiFileContentMetaData {
   private val bytes: ByteArray = contents.copyOf()
   override fun getBytes(): ByteArray {
      return bytes.copyOf()
   }
}

open class TiFileContentsBytes(contents: ByteArray) : TiFileContents {

   companion object {
      fun create(constants: List<Constant>, fileType: FileType = FileType.INTERNAL): TiFileContentsBytes {
         val byteArrayList = constants.map { constant -> constant.toFileRepresentation(fileType) }
         val overallSize = byteArrayList.map { array -> array.size }.sum()
         val resultContents = ByteArray(overallSize)
         var overallOffset = 0
         for (byteArray in byteArrayList) {
            byteArray.copyInto(resultContents, overallOffset)
            overallOffset += byteArray.size
         }
         return TiFileContentsBytes(resultContents)
      }
   }

   private val bytes: ByteArray = contents.copyOf()

   override val length = bytes.size

   override fun getBytes(): ByteArray {
      return bytes.copyOf()
   }

   override fun iterateBytes(): ByteIterator {
      return bytes.iterator()
   }

   override fun readByte(offset: Int): Byte {
      checkOffset(offset)
      return bytes[offset]
   }

   override fun readBytes(startOffset: Int, resultLength: Int): ByteArray {
      checkOffset(startOffset)
      val offsetAfterResult = startOffset + resultLength
      checkOffset(offsetAfterResult - 1)
      return bytes.copyOfRange(startOffset, offsetAfterResult)
   }

   // HELPERS //

   private fun checkOffset(offset: Int) {
      if (offset !in bytes.indices) throw InputError()
   }
}

private fun Constant.toFileRepresentation(fileType: FileType): ByteArray {
   if (fileType == FileType.DISPLAY) TODO("Not yet implemented: $fileType")
   return toInternal()
}

private fun Constant.toInternal(): ByteArray {
   return when (constant) {
      is String -> {
         val string = constant as String
         ByteArray(string.length + 1) { byteOffset ->
            when (byteOffset) {
               0 -> string.length.toByte()
               in 1..string.length -> string[byteOffset - 1].toByte()
               else -> error("Illegal string byte representation offset: $byteOffset")
            }
         }
      }
      is Number -> {
         val numberValue = constant as Number
         val zeroExponent: Byte = 0x40
         val numberBytes = ByteArray(8)
         numberBytes[0] = zeroExponent
         // TODO: Enhance to real representation of all bytes
         numberBytes[1] = numberValue.toByte()
         val result = numberBytes.copyInto(ByteArray(numberBytes.size + 1), 1)
         result[0] = numberBytes.size.toByte()
         return result
      }
      else -> throw NotImplementedError("Cannot represent constant in file: $constant")
   }
}

private fun NumericConstant.Companion.fromInternal(bytes: ByteArray): NumericConstant {
   return NumericConstant(bytes[1]) // TODO: Enhance to real representation of all bytes
}

