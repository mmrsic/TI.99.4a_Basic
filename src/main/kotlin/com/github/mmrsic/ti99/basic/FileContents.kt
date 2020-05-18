package com.github.mmrsic.ti99.basic

import com.github.mmrsic.ti99.basic.expr.Constant
import com.github.mmrsic.ti99.basic.expr.NumericConstant
import com.github.mmrsic.ti99.basic.expr.StringConstant

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

/**
 * A [TiFileContentMetaData] for th TI FILES format.
 */
class TiFileContentMetaDataTiFiles(contents: ByteArray) : TiFileContentMetaData {

   private val bytes: ByteArray = contents.copyOf()

   override fun getBytes(): ByteArray {
      return bytes.copyOf()
   }
}

/**
 * A [TiFileContents] where the contents is represented by a byte array.
 */
open class TiFileContentsBytes(contents: ByteArray) : TiFileContents {

   companion object {
      /**
       * Create a [TiFileContentsBytes] from a list of records consisting of a list on [Constant]s where each record is of a
       * given fixed size.
       * @param constants the list of records consisting of a list of constants for each record
       * @param recordSize fixed size of each record
       * @param fileType file type used to encode the constants
       */
      fun createFixed(constants: List<List<Constant>>, recordSize: Int = 64, fileType: FileType = FileType.INTERNAL): TiFileContentsBytes {
         val overallSize = constants.size * recordSize
         val resultContents = ByteArray(overallSize)
         constants.withIndex().forEach {
            val recordByteArrayList = it.value.map { constant ->
               when (constant) {
                  is NumericConstant -> fileType.encoder().encode(constant)
                  is StringConstant -> fileType.encoder().encode(constant)
                  else -> error("Don't know hou to map constant $constant to byte array")
               }
            }
            var overallOffset = it.index * recordSize
            for (byteArray in recordByteArrayList) {
               byteArray.copyInto(resultContents, overallOffset)
               overallOffset += byteArray.size
            }
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