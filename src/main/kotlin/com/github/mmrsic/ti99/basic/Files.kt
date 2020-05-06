package com.github.mmrsic.ti99.basic

import com.github.mmrsic.ti99.basic.FileType.DISPLAY
import com.github.mmrsic.ti99.basic.FileType.INTERNAL
import com.github.mmrsic.ti99.basic.OpenMode.APPEND
import com.github.mmrsic.ti99.basic.OpenMode.INPUT
import com.github.mmrsic.ti99.basic.OpenMode.OUTPUT
import com.github.mmrsic.ti99.basic.OpenMode.UPDATE

/**
 * An accessory device is able to manage files, accessible from TI Basic.
 */
interface AccessoryDevice {

   /** Open a file on this device, that is, make it accessible by TI Basic. */
   fun open(name: String, options: FileOpenOptions): TiBasicFile {
      return object : TiBasicFile {

         override fun open(options: FileOpenOptions) {
            TODO("not implemented")
         }

         override fun getNextString(): String {
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

/**
 * Represents a single file on an [AccessoryDevice].
 */
interface TiBasicFile {

   /** Open this file for access with a given set of [FileOpenOptions]. */
   fun open(options: FileOpenOptions)

   /** Get the next datum as a string */
   fun getNextString(): String

   /** Close this file previously opened with [AccessoryDevice.open]. */
   fun close()

   /** Delete this file. */
   fun delete()
}

// TODO: Introduce interfaces and classes for TiBasicFileContent, TiBasicFileByteContent, TiBasicFileAsciiContent
// and adhere to specification in User's Reference Guide at page II-126.

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
data class FileOrganization(val type: Type = Type.SEQUENTIAL, val initialNumberOfRecords: Int? = null) :
        FileOpenOption {

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
   DISPLAY,

   /**
    * INTERNAL-type data is recorded in internal machine format which has not been translated into printable
    * characters. Data in this form can be read easily by the computer but not by people.
    */
   INTERNAL;
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
}

/**
 * Whether the records on a file are all of the same length or vary in length.
 * @param lengthType type of the record length
 * @param length (maximum) length of any record
 */
data class RecordType(val lengthType: LengthType, val length: Int?) : FileOpenOption {

   /** Length type information of a [RecordType]. */
   enum class LengthType {

      /** Fixed record length, that is, each record of the file has the same length. */
      FIXED,

      /** Variable record length, that is, each record of the file may have a different length. */
      VARIABLE;
   }
}
