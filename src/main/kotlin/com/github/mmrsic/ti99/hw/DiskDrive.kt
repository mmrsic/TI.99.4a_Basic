package com.github.mmrsic.ti99.hw

import com.github.mmrsic.ti99.basic.AccessoryDevice
import com.github.mmrsic.ti99.basic.FileOpenOptions
import com.github.mmrsic.ti99.basic.TiBasicFile
import com.github.mmrsic.ti99.basic.TiFileContents
import com.github.mmrsic.ti99.basic.expr.NumericConstant
import com.github.mmrsic.ti99.basic.expr.StringConstant
import java.nio.charset.StandardCharsets

class AccessoryDeviceDiskDrive(val number: Int) : AccessoryDevice {

   private val files: MutableMap<String, TiDiskDriveFile> = sortedMapOf()

   override val id: String
      get() = PREFIX + number

   fun saveFiles(newFiles: List<TiDiskDriveFile>) {
      newFiles.forEach { files[it.name] = it }
      println("Saved files: ${files.keys}")
   }

   override fun open(fileName: String, options: FileOpenOptions): TiBasicFile {
      try {
         return files.getValue(fileName)
      } catch (e: NoSuchElementException) {
         println("No file named '$fileName' in $id: ${files.keys}")
         throw NoSuchElementException(fileName)
      }
   }

   companion object {

      /**Prefix for all disk drive names. */
      const val PREFIX = "DSK"

      /** Create a TI Basic ID for a Disk Drive, e.g. "DSK1". */
      fun createId(number: Int): String = "$PREFIX$number"

   }
}

class TiDiskDriveFile(val name: String, val contents: TiFileContents) : TiBasicFile {

   private var nextDataPointer = 0

   override fun open(options: FileOpenOptions) {
      TODO("not implemented")
   }

   override fun getNextString(): StringConstant {
      return StringConstant(String(getNextByteChunk(), StandardCharsets.US_ASCII))
   }

   override fun getNextNumber(): NumericConstant {
      val numberBytes = getNextByteChunk()
      return NumericConstant(numberBytes[1]) // TODO: Enhance to use real representation including all bytes
   }

   override fun isEndOfFile(): NumericConstant {
      return if (nextDataPointer >= contents.length) NumericConstant(-1) else NumericConstant.ZERO
   }

   override fun close() {
      println("Closed: disk drive file '$name'")
   }

   override fun delete() {
      TODO("not implemented")
   }

   // HELPERS //

   private fun getNextByteChunk(): ByteArray {
      val resultLength: Int = contents.readByte(nextDataPointer++).toInt()
      val resultBytes = contents.readBytes(nextDataPointer, resultLength)
      nextDataPointer += resultLength
      return resultBytes
   }
}