package com.github.mmrsic.ti99

import com.github.mmrsic.ti99.hw.TiBasicModule
import kotlin.test.assertEquals

/**
 * Helpers to check file contents handled by a [TiBasicModule] in test cases.
 */
class TestHelperFile {

   companion object {

      /** Assert that a file handler for a given [fileNumber] currently handles a file with a given byte array contents. */
      fun assertFileRecords(expectedFileRecordBytes: List<List<ByteArray>>, machine: TiBasicModule, fileNumber: Int) {
         val actualFileRecordBytes = machine.getFileHandlerRecords(fileNumber).map { byteArray ->
            readAllLengthPrecededByteArrays(byteArray)
         }
         assertEquals(expectedFileRecordBytes.size, actualFileRecordBytes.size, "Number of records")
         for (recordIdx in expectedFileRecordBytes.indices) {
            val expectedAtIdx = expectedFileRecordBytes[recordIdx]
            val actualAtIdx = actualFileRecordBytes[recordIdx]
            try {
               assertEqualByteArrayLists(expectedAtIdx, actualAtIdx)
            } catch (e: AssertionError) {
               throw AssertionError("File $fileNumber: Byte array list at record no. $recordIdx")
            }
         }
      }

      /** Assert that two given lists contain equals byte arrays. */
      fun assertEqualByteArrayLists(expectedAtIdx: List<ByteArray>, actualAtIdx: List<ByteArray>) {
         assertEquals(expectedAtIdx.size, actualAtIdx.size, "Number of record byte arrays")
         for (itemIdx in expectedAtIdx.indices) {
            val expectedBytes = expectedAtIdx[itemIdx].toList()
            val actualBytes = actualAtIdx[itemIdx].toList()
            assertEquals(expectedBytes, actualBytes)
         }
      }

      /** Split a given byte array into all length-preceded byte arrays. */
      fun readAllLengthPrecededByteArrays(byteArray: ByteArray): List<ByteArray> {
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
   }
}