package com.github.mmrsic.ti99.hw

import com.github.mmrsic.ti99.basic.AccessoryDevice
import com.github.mmrsic.ti99.basic.FileError
import com.github.mmrsic.ti99.basic.FileOpenOptions
import com.github.mmrsic.ti99.basic.FileOrganization
import com.github.mmrsic.ti99.basic.FileType
import com.github.mmrsic.ti99.basic.InputError
import com.github.mmrsic.ti99.basic.OpenMode
import com.github.mmrsic.ti99.basic.RecordType
import com.github.mmrsic.ti99.basic.TiBasicFile
import com.github.mmrsic.ti99.basic.betterparse.TiBasicParser
import com.github.mmrsic.ti99.basic.expr.Constant
import com.github.mmrsic.ti99.basic.expr.Expression
import com.github.mmrsic.ti99.basic.expr.NumericConstant
import com.github.mmrsic.ti99.basic.expr.PrintSeparator
import com.github.mmrsic.ti99.basic.expr.StringConstant

/**
 * Cassette Recorder [AccessoryDevice], connected to a [TiBasicModule].
 * @param id ID of the cassette recorder - must be either "CS1" or "CS2"
 */
class AccessoryDeviceCassetteRecorder(override val id: String, private val machine: TiBasicModule) : AccessoryDevice {

   companion object {
      const val PREFIX: String = "CS"
   }

   private val allowedDeviceNames = IntRange(1, 2).map { PREFIX + it }
   private val allowedOrganizations = listOf(FileOrganization.Type.SEQUENTIAL)
   private val allowedTypes = listOf(FileType.INTERNAL, FileType.DISPLAY)
   private val allowedModes = listOf(OpenMode.INPUT, OpenMode.OUTPUT)
   private val allowedLengthTypes = listOf(RecordType.LengthType.FIXED)

   init {
      if (id !in allowedDeviceNames) throw  IllegalArgumentException("Illegal cassette recorder ID: $id")
   }

   override fun open(fileName: String, options: FileOpenOptions): TiBasicFile {
      checkFileName(fileName)
      checkOrganization(options.organization.type)
      checkFileType(options.fileType)
      checkMode(options.mode)
      checkRecordType(options.recordType)

      machine.printTokens(listOf(PrintSeparator.NextRecord))
      printMessage(id, "REWIND CASSETTE TAPE")
      printMessage(id, "PRESS CASSETTE ${modeToButton(options.mode)}")

      if (tape == null) insertTape("")
      val result = tape!!
      result.open(options)
      return result
   }

   /** Currently inserted tape. */
   private var tape: TiBasicFileCassetteRecorder? = null

   /**
    * Insert a cassette tape with its data given as String into this [AccessoryDeviceCassetteRecorder]. Any previously
    * inserted tape will be removed.
    */
   fun insertTape(tapeDisplayData: String) {
      tape = TiBasicFileCassetteRecorder(id, tapeDisplayData, machine)
      println("$id: Inserted tape: $tapeDisplayData")
   }

   /** Remove any previously inserted tape from this [AccessoryDeviceCassetteRecorder]. */
   fun removeTape() {
      tape = null
      println("$id: Removed tape")
   }

   // HELPERS //

   private fun checkFileName(nativeFileName: String) {
      if (nativeFileName != "") throw IllegalArgumentException("Illegal file name: $nativeFileName")
   }

   private fun checkOrganization(organization: FileOrganization.Type) {
      if (organization !in allowedOrganizations) throw IllegalArgumentException("Illegal file organization: $organization")
   }

   private fun checkFileType(fileType: FileType) {
      if (fileType !in allowedTypes) throw IllegalArgumentException("Illegal file type: $fileType")
   }

   private fun checkMode(mode: OpenMode) {
      if (mode !in allowedModes) throw IllegalArgumentException("Illegal mode: $mode")
   }

   private fun checkRecordType(recordType: RecordType) {
      if (recordType.lengthType !in allowedLengthTypes) throw IllegalArgumentException("Illegal record type: $recordType")
   }

   private fun printMessage(id: String, msg: String) = machine.printTokens(printTokensForRecorderAction(id, msg))
   private fun modeToButton(mode: OpenMode) = if (mode == OpenMode.INPUT) "PLAY" else "RECORD"
}

/**
 * A [TiBasicFile] on a [AccessoryDeviceCassetteRecorder].
 * @param id either "CS1" or "CS2", as the device also represents the "file"
 */
class TiBasicFileCassetteRecorder(val id: String, displayData: String = "", private val machine: TiBasicModule) :
   TiBasicFile {

   private var options: FileOpenOptions? = null

   private val data: List<Constant> by lazy { TiBasicParser(machine).parseConstantsList(displayData) }
   private var dataIndex = 0

   override fun open(options: FileOpenOptions) {
      this.options = options
   }

   override fun getNextString() = getNextConstant() as? StringConstant ?: throw InputError()
   override fun getNextNumber() = getNextConstant() as? NumericConstant ?: throw InputError()
   override fun isEndOfFile() = NumericConstant.ZERO

   override fun close() {
      machine.printTokens(listOf(PrintSeparator.NextRecord))
      machine.printTokens(printTokensForRecorderAction(id, "PRESS CASSETTE STOP"))
      options = null
   }

   override fun delete() = println("Delete is ignored for Cassette Recorder")

   // HELPERS //

   private fun getNextConstant(): Constant {
      if (dataIndex !in data.indices) throw FileError()
      return data[dataIndex++]
   }

}

/**
 * All the print tokens used for a single instruction message for usage of an cassette recorder.
 * @param id device ID
 * @param message the message to display, for example "PRESS CASSETTE STOP" - will automatically augmented with
 * additional texts and print separators
 */
private fun printTokensForRecorderAction(id: String, message: String): List<Expression> {
   val fillSpaces = " ".repeat(TiBasicScreen.NUM_PRINT_COLUMNS - 2 - message.length - id.length)
   return listOf(
      PrintSeparator.NextRecord, StringConstant("* $message"), PrintSeparator.Adjacent,
      StringConstant(fillSpaces), PrintSeparator.Adjacent, StringConstant(id), PrintSeparator.NextRecord,
      StringConstant("  THEN PRESS ENTER")
   )
}