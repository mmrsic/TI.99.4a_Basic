package com.github.mmrsic.ti99.hw

import com.github.mmrsic.ti99.basic.AccessoryDevice
import com.github.mmrsic.ti99.basic.FileOpenOptions
import com.github.mmrsic.ti99.basic.TiDataFile
import com.github.mmrsic.ti99.basic.TiFileContents

/** An [AccessoryDevice] simulating a disk drive. */
class DiskDriveAccessoryDevice(val number: Int) : AccessoryDevice {

   private val files: MutableMap<String, TiDiskDriveDataFile> = sortedMapOf()

   override val id: String get() = PREFIX + number

   /** Save a given list of disk drive files to this accessory device. */
   fun saveFiles(newFiles: List<TiDiskDriveDataFile>) {
      newFiles.forEach { files[it.name] = it }
      println("Saved files: ${files.keys}")
   }

   override fun open(fileName: String, options: FileOpenOptions): TiDataFile {
      try {
         val result = files.getValue(fileName)
         result.open(options)
         return result
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

/** A single [TiDataFile] which is located on a disk drive. */
class TiDiskDriveDataFile(val name: String, val contents: TiFileContents) : TiDataFile(contents.getBytes()) {

   override fun open(options: FileOpenOptions) {

   }

   override fun close() {
      println("Closed: disk drive file '$name'")
   }

   override fun delete() {
      TODO("not implemented")
   }

}