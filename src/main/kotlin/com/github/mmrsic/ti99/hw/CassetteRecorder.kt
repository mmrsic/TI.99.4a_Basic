package com.github.mmrsic.ti99.hw

import com.github.mmrsic.ti99.basic.AccessoryDevice
import com.github.mmrsic.ti99.basic.FileOpenOptions
import com.github.mmrsic.ti99.basic.FileOrganization
import com.github.mmrsic.ti99.basic.FileType
import com.github.mmrsic.ti99.basic.OpenMode
import com.github.mmrsic.ti99.basic.RecordType
import com.github.mmrsic.ti99.basic.TiBasicFile
import com.github.mmrsic.ti99.basic.expr.Expression
import com.github.mmrsic.ti99.basic.expr.PrintSeparator
import com.github.mmrsic.ti99.basic.expr.StringConstant

/**
 * Cassette Recorder [AccessoryDevice], connected to a [TiBasicModule].
 */
class AccessoryDeviceCassetteRecorder(private val machine: TiBasicModule) : AccessoryDevice {
    private val allowedDeviceNames = listOf("CS1", "CS2")
    private val allowedOrganizations = listOf(FileOrganization.Type.SEQUENTIAL)
    private val allowedTypes = listOf(FileType.INTERNAL, FileType.DISPLAY)
    private val allowedModes = listOf(OpenMode.INPUT, OpenMode.OUTPUT)
    private val allowedLengthTypes = listOf(RecordType.LengthType.FIXED)

    override fun open(name: StringConstant, options: FileOpenOptions): TiBasicFile {
        val nativeName = name.toNative()
        checkDeviceName(nativeName)
        checkOrganization(options.organization.type)
        checkFileType(options.fileType)
        checkMode(options.mode)
        checkRecordType(options.recordType)

        machine.printTokens(listOf(PrintSeparator.NextRecord))
        printMessage(nativeName, "REWIND CASSETTE TAPE")
        printMessage(nativeName, "PRESS CASSETTE ${modeToButton(options.mode)}")

        return TiBasicFileCassetteRecorder(nativeName, machine)
    }

    private fun checkDeviceName(nativeCassetteRecorderName: String) {
        if (nativeCassetteRecorderName !in allowedDeviceNames)
            throw IllegalArgumentException("Illegal device name: $nativeCassetteRecorderName")
    }

    private fun checkOrganization(organization: FileOrganization.Type) {
        if (organization !in allowedOrganizations)
            throw IllegalArgumentException("Illegal file organization: $organization")
    }

    private fun checkFileType(fileType: FileType) {
        if (fileType !in allowedTypes) throw IllegalArgumentException("Illegal file type: $fileType")
    }

    private fun checkMode(mode: OpenMode) {
        if (mode !in allowedModes) throw IllegalArgumentException("Illegal mode: $mode")
    }

    private fun checkRecordType(recordType: RecordType) {
        if (recordType.lengthType !in allowedLengthTypes)
            throw IllegalArgumentException("Illegal record type: $recordType")
    }

    private fun printMessage(id: String, msg: String) = machine.printTokens(printTokensForRecorderAction(id, msg))
    private fun modeToButton(mode: OpenMode) = if (mode == OpenMode.INPUT) "PLAY" else "RECORD"
}

/**
 * A [TiBasicFile] on a [AccessoryDeviceCassetteRecorder].
 * @param id either "CS1" or "CS2", as the device also represents the "file"
 */
class TiBasicFileCassetteRecorder(val id: String, private val machine: TiBasicModule) : TiBasicFile {

    override fun close() {
        machine.printTokens(listOf(PrintSeparator.NextRecord))
        machine.printTokens(printTokensForRecorderAction(id, "PRESS CASSETTE STOP"))
    }

    override fun delete() = println("Delete is ignored for Cassette Recorder")
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