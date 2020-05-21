package com.github.mmrsic.ti99.basic

import com.github.mmrsic.ti99.basic.FileType.DISPLAY
import com.github.mmrsic.ti99.basic.FileType.INTERNAL
import com.github.mmrsic.ti99.basic.OpenMode.APPEND
import com.github.mmrsic.ti99.basic.OpenMode.INPUT
import com.github.mmrsic.ti99.basic.OpenMode.OUTPUT
import com.github.mmrsic.ti99.basic.OpenMode.UPDATE
import com.github.mmrsic.ti99.basic.betterparse.TiBasicParser
import com.github.mmrsic.ti99.basic.expr.Constant
import com.github.mmrsic.ti99.basic.expr.NumericConstant
import com.github.mmrsic.ti99.basic.expr.PrintConstant
import com.github.mmrsic.ti99.basic.expr.PrintSeparator
import com.github.mmrsic.ti99.basic.expr.StringConstant
import com.github.mmrsic.ti99.hw.TiBasicModule
import com.github.mmrsic.ti99.hw.Variable
import kotlin.math.ceil
import kotlin.math.min

/**
 * An accessory device is able to manage files, accessible from TI Basic.
 */
interface AccessoryDevice {

   /** Unique ID of this [AccessoryDevice]. */
   val id: String

   /** Open a file on this device, that is, make it accessible by TI Basic. */
   fun open(fileName: String, options: FileOpenOptions): TiDataFile {
      return object : TiDataFile() {

         override fun open(options: FileOpenOptions) {
            TODO("not implemented")
         }

         override fun close() {
            TODO("not implemented")
         }

         override fun delete() {
            TODO("not implemented")
         }
      }
   }
}

/** A file as used by a TI Basic module. */
interface TiFile {

   /** Represent the contents of this [TiFile] as a [ByteArray]. */
   fun toByteArray(): ByteArray

   /** Represent a part of the contents of this [TiFile] as a [ByteArray]. */
   fun toByteArray(offset: Int, size: Int): ByteArray
}

/**
 * Represents a single data file on an [AccessoryDevice].
 */
abstract class TiDataFile(initialContents: ByteArray = ByteArray(0)) : TiFile {

   private var contents: ByteArray = ByteArray(initialContents.size).apply {
      initialContents.copyInto(this)
   }

   /** Current size of this [TiDataFile].*/
   val size: Int get() = contents.size

   override fun toByteArray() = contents.copyOf()
   override fun toByteArray(offset: Int, size: Int) = contents.copyOfRange(offset, offset + size)

   /** Open this file for access with a given set of [FileOpenOptions]. */
   abstract fun open(options: FileOpenOptions)

   /**
    *  Change the length of this file to a given new value. If the [newLength] is smaller than [getLength], the data following
    *  the new length is lost. All data up to the new length is preserved.
    */
   fun setLength(newLength: Int) {
      val oldLength = getLength()
      if (newLength == oldLength) return
      if (newLength < oldLength) println("Shortening $this from $oldLength to $newLength bytes")
      val oldContents = contents
      contents = ByteArray(newLength)
      oldContents.copyInto(contents, endIndex = min(newLength, oldLength))
   }

   /**
    * The current length of this [TiDataFile], that is, the last value set with [setLength] or, if never set, the length of the
    * byte array provided at creation time.
    */
   fun getLength() = contents.size

   /** Write a given byte array to a specific position given by its offset from the beginning of this [TiDataFile]. */
   fun write(offset: Int, bytes: ByteArray) {
      bytes.copyInto(contents, offset)
   }

   /** Read a single byte from this file at a specific position given by its offset from the beginning of this [TiDataFile]. */
   fun read(offset: Int): Byte = contents[offset]

   /**
    * Read a byte array from this file beginning at a specific position given by its offset from the beginnung of this
    * [TiDataFile].
    * @param offset has to by between zero and less than the size of this file
    * @param size size of the resulting byte array - must be less than or equal to the size of this file when added to the
    * specified offset
    */
   fun read(offset: Int, size: Int): ByteArray = contents.copyOfRange(offset, offset + size)

   /** Close this file previously opened with [AccessoryDevice.open]. */
   abstract fun close()

   /** Delete this file. */
   abstract fun delete()
}

/* A file handler for [TiDataFile]s. */
class TiDataFileHandler(val file: TiDataFile, val openOptions: FileOpenOptions) {

   private val records: MutableList<TiDataRecord> = mutableListOf<TiDataRecord>().apply {
      when {
         openOptions.recordType.isFixed -> {
            val recordSize = openOptions.recordType.length ?: 64
            val numRecords = ceil(file.size.toDouble() / recordSize).toInt()
            if (file.size < numRecords * recordSize) file.setLength(numRecords * recordSize)
            (0 until numRecords).forEach { recordNumber ->
               add(TiDataRecord(file, recordNumber * recordSize, recordSize))
            }
         }
         openOptions.fileType == INTERNAL -> {
            var fileOffset = 0
            separateByInternalFormat(file.toByteArray()).forEachIndexed { recordNumber, bytes ->
               add(TiDataRecord(file, fileOffset, bytes.size))
               fileOffset += bytes.size
            }
         }
         else -> {
            var overallFileSize = 0
            TiBasicParser(TiBasicModule()).parseConstantsList(file.toByteArray().toString(TiBasicEncoding.CHARSET))
               .forEachIndexed { recordNumber, constant ->
                  val recordSize = if (constant is StringConstant) constant.constant.length + 1 else 9
                  add(TiDataRecord(file, overallFileSize, recordSize))
                  overallFileSize += recordSize
               }
         }
      }
   }
   private val currRec = records.listIterator()
   private val writer: TiDataRecordWriter? = if (openOptions.mode.isWrite()) TiDataRecordWriter() else null
   private val encoder: TiDataEncoder = TiDataEncoder(openOptions.fileType)
   private val reader: TiDataRecordReader? = if (openOptions.mode.isRead()) TiDataRecordReader() else null
   private val decoder: TiDataDecoder = TiDataDecoder(openOptions.fileType)

   /** Write a given list of [PrintConstant]s as a single record. */
   fun writeRecord(recordData: List<PrintConstant>) {
      if (writer == null) throw FileError()
      if (!currRec.hasNext()) {
         val oldFileSize = file.size
         val nextRecordSize = openOptions.recordType.length ?: 64
         file.setLength(oldFileSize + nextRecordSize)
         val newRecord = TiDataRecord(file, oldFileSize, nextRecordSize)
         newRecord.write(ByteArray(nextRecordSize) { openOptions.fileType.fillerByte })
         currRec.add(newRecord)
         currRec.previous()
      }
      writer.write(currRec.next(), encoder.encodeAll(recordData))
      if (recordData.last() is PrintSeparator) {
         currRec.previous()
      }
   }

   /** Read a single record into a given list of variables. */
   fun readRecord(variables: List<Variable>): List<Constant> {
      if (reader == null) throw FileError()
      if (!currRec.hasNext()) throw InputError()
      val recordByteList = when (openOptions.fileType) {
         INTERNAL -> reader.read(currRec.next())
         DISPLAY -> encoder.encodeAll(
            TiBasicParser(TiBasicModule()).parseConstantsList(
               currRec.next().bytes.toString(TiBasicEncoding.CHARSET).trim(0.toChar())))
      }
      return variables.withIndex().map { indexedVariable ->
         when {
            indexedVariable.value.isNumeric() -> try {
               decoder.decodeNumber(recordByteList[indexedVariable.index])
            } catch (e: NumberFormatException) {
               throw InputError()
            }
            indexedVariable.value.isString() -> decoder.decodeString(recordByteList[indexedVariable.index])
            else -> error("Not implemented: Decode variable named '${indexedVariable.value.name}'")
         }
      }
   }

   /** Check whether this handler is currently at the logical or physical end of its file. */
   fun isEndOfFile(): NumericConstant {
      return NumericConstant.ONE
   }

   /** The records managed by this file handler converted to a list of byte arrays. */
   fun getRecordByteArrays(): List<ByteArray> = records.map { record -> record.bytes }

   /** Close the file of this file handler. */
   fun close() {
      file.close()
   }
}

/** Representation of a file buffer, containing a part of a [TiDataFile]. */
class TiDataRecord(private val file: TiDataFile, private val fileOffset: Int, private val recordSize: Int) {

   /** The current bytes contained in this [TiDataRecord]. */
   val bytes get() = file.toByteArray(fileOffset, recordSize)

   /**
    * Write a given byte array to this [TiDataRecord], beginning at a given additional offset within this record.
    * @param bytes the contents to write to this record
    * @param additionalOffset offset within this record where to place the first byte of the specified byte array - must be >= 0
    * and less than record size minus byte array size
    */
   fun write(bytes: ByteArray, additionalOffset: Int = 0) {
      file.write(fileOffset + additionalOffset, bytes)
   }
}

/** An encoder to TI Basic data which may be written to a file. */
class TiDataEncoder(private val fileType: FileType) {

   /** Encode all [PrintConstant] of a given list as a list of byte arrays. */
   fun encodeAll(constants: List<PrintConstant>): List<ByteArray> {
      val result = mutableListOf<ByteArray>()
      for (constant in constants) {
         when (constant) {
            is NumericConstant -> result.add(encode(constant))
            is StringConstant -> result.add(encode(constant))
            else -> println("Ignored: $constant")
         }
      }
      return result
   }

   /** Encode a given string constant into a byte array. */
   fun encode(string: StringConstant) = fileType.encoder().encode(string)

   /** Encode a given numeric constant into a byte array. */
   fun encode(number: NumericConstant) = fileType.encoder().encode(number)
}

/** Decoder for [NumericConstant]s and [StringConstant]s. */
class TiDataDecoder(private val fileType: FileType) {

   fun decodeNumber(bytes: ByteArray): NumericConstant = fileType.decoder().decodeNumber(bytes)
   fun decodeString(bytes: ByteArray): StringConstant = fileType.decoder().decodeString(bytes)
}

/** Writer for [TiDataRecord]s. */
class TiDataRecordWriter {

   /** Write given data to a give record. */
   fun write(record: TiDataRecord, recordData: List<ByteArray>) {
      var offset = 0
      recordData.forEach {
         record.write(it, offset)
         offset += it.size
      }
   }
}

/** Reader of [TiDataRecord]s. */
class TiDataRecordReader {

   /** Read a given [TiDataRecord] and convert its contents into a list of byte arrays. */
   fun read(record: TiDataRecord): List<ByteArray> {
      val byteArray = record.bytes
      return separateByInternalFormat(byteArray)
   }
}

/**
 * Options used when a file is opened.
 */
interface FileOpenOptions {

   /** Organization of the file. */
   val organization: FileOrganization

   /** Type of the file, that is, the encoding used. */
   val fileType: FileType

   /** Mode of operation. */
   val mode: OpenMode

   /** Type for all records within the file. */
   val recordType: RecordType
}

/** Represents an option usable when opening a file. */
interface FileOpenOption

/**
 * Files used in TI Basic can be organized either sequentially or randomly. Records on a sequential file are read
 * or written one after the other in sequence from the beginning to end. Random-access files (called RELATIVE in TI
 * Basic) can be read or written in any record order. They may also be processed sequentially.
 * @param type access type
 * @param initialNumberOfRecords number of already present records
 */
data class FileOrganization(val type: Type = Type.SEQUENTIAL, val initialNumberOfRecords: Int? = null) : FileOpenOption {

   val isRelative = type == Type.RELATIVE
   val isSequential = type == Type.SEQUENTIAL

   /** Access type of the [FileOrganization]. */
   enum class Type {

      /** Sequential access only. */
      SEQUENTIAL,

      /** Relative access, that is, random access. */
      RELATIVE;
   }
}

/** Whether a file is of [DISPLAY] or of [INTERNAL] type. */
enum class FileType : FileOpenOption {

   /**
    * The DISPLAY-type format refers to printable (ASCII) characters. The DISPLAY format is normally used when the
    * output will be read by people, rather than by the computer. Each DISPLAY-type record usually corresponds to one
    * print line.
    */
   DISPLAY {

      override fun encoder() = Encoder.DISPLAY
      override fun decoder() = Decoder.DISPLAY
      override val fillerByte = ' '.toByte()
   },

   /**
    * INTERNAL-type data is recorded in internal machine format which has not been translated into printable
    * characters. Data in this form can be read easily by the computer but not by people.
    */
   INTERNAL {

      override fun encoder() = Encoder.INTERNAL
      override fun decoder() = Decoder.INTERNAL
      override val fillerByte = 0.toByte()
   };

   abstract fun encoder(): Encoder
   abstract fun decoder(): Decoder
   abstract val fillerByte: Byte
}

/** Whether to process a file in [INPUT], [OUTPUT], [UPDATE], or [APPEND] mode. */
enum class OpenMode : FileOpenOption {

   /** INPUT files may be read only. */
   INPUT,

   /** OUTPUT files may be written only. */
   OUTPUT,

   /** UPDATE files may be both read and written. */
   UPDATE,

   /**
    * APPEND mode allows data to be added at the end of an existing file. The records already on the file cannot be
    * accessed in this mode.
    */
   APPEND;

   fun isWrite(): Boolean = this in listOf(OUTPUT, UPDATE, APPEND)
   fun isRead(): Boolean = this in listOf(INPUT, UPDATE)
}

/**
 * Whether the records on a file are all of the same length or vary in length.
 * @param lengthType type of the record length
 * @param length (maximum) length of any record
 */
data class RecordType(val lengthType: LengthType, val length: Int?) : FileOpenOption {

   val isFixed = lengthType == LengthType.FIXED
   val isVariable = lengthType == LengthType.VARIABLE

   /** Length type information of a [RecordType]. */
   enum class LengthType {

      /** Fixed record length, that is, each record of the file has the same length. */
      FIXED,

      /** Variable record length, that is, each record of the file may have a different length. */
      VARIABLE;
   }
}

// HELPERS //

private fun separateByInternalFormat(byteArray: ByteArray): List<ByteArray> {
   return mutableListOf<ByteArray>().apply {
      var nextLengthByte = 0
      while (nextLengthByte < byteArray.size) {
         val length = byteArray[nextLengthByte]
         if (length <= 0) break
         val previousLengthByte = nextLengthByte
         nextLengthByte += length + 1
         add(byteArray.copyOfRange(previousLengthByte, nextLengthByte))
      }
   }.toList()
}