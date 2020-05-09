package com.github.mmrsic.ti99.basic

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

   /** All the bytes of this [TiFileContents]. */
   fun getBytes(): ByteArray

   /** An iterator over [getBytes]. */
   fun iterateBytes(): ByteIterator
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
      if (isRecognized(bytes)) TiFileContentsTiFiles(bytes.copyOfRange(0, headerLength)) else null
}

class TiFileContentMetaDataTiFiles(private val bytes: ByteArray) : TiFileContentMetaData {
   override fun getBytes(): ByteArray {
      return bytes.copyOf()
   }
}

class TiFileContentsTiFiles(private val bytes: ByteArray) : TiFileContents {
   override fun getBytes(): ByteArray {
      return bytes.copyOf()
   }

   override fun iterateBytes(): ByteIterator {
      return bytes.iterator()
   }
}

