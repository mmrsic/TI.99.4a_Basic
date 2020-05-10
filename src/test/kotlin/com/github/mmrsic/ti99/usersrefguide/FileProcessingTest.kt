package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.basic.TiFileContentsBytes
import com.github.mmrsic.ti99.basic.expr.NumericConstant
import com.github.mmrsic.ti99.basic.expr.StringConstant
import com.github.mmrsic.ti99.hw.TiBasicModule
import com.github.mmrsic.ti99.hw.TiDiskDriveFile
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/**
 * Test cases for examples found in User's Reference Guide on pages II-118 through II-136.
 */
class FileProcessingTest {

   @Test
   fun testOpenCs1ForInput() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 OPEN #2:"CS1",SEQUENTIAL,INTERNAL,INPUT,FIXED 128,PERMANENT
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            19 to "  TI BASIC READY",
            21 to " >100 OPEN #2:\"CS1\",SEQUENTIAL",
            22 to "  ,INTERNAL,INPUT,FIXED 128,PE",
            23 to "  RMANENT",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testOpenCs1ForInputAndCs2ForOutput() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 OPEN #25:"CS1",SEQUENTIAL,INTERNAL,INPUT,FIXED,PERMANENT
         110 X=100
         120 OPEN #X+5:"CS2",SEQUENTIAL,INTERNAL,OUTPUT,FIXED,PERMANENT
         130 N=2
         140 OPEN #122:"CS"&STR$(N),SEQUENTIAL,INTERNAL,OUTPUT,FIXED,PERMANENT
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            11 to "  TI BASIC READY",
            13 to " >100 OPEN #25:\"CS1\",SEQUENTIA",
            14 to "  L,INTERNAL,INPUT,FIXED,PERMA",
            15 to "  NENT",
            16 to " >110 X=100",
            17 to " >120 OPEN #X+5:\"CS2\",SEQUENTI",
            18 to "  AL,INTERNAL,OUTPUT,FIXED,PER",
            19 to "  MANENT",
            20 to " >130 N=2",
            21 to " >140 OPEN #122:\"CS\"&STR\$(N),S",
            22 to "  EQUENTIAL,INTERNAL,OUTPUT,FI",
            23 to "  XED,PERMANENT",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testOpenCs2ForOutput() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("100 OPEN #4:\"CS2\",OUTPUT,INTERNAL,SEQUENTIAL,FIXED", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            20 to "  TI BASIC READY",
            22 to " >100 OPEN #4:\"CS2\",OUTPUT,INT",
            23 to "  ERNAL,SEQUENTIAL,FIXED",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testOpenNameVariable() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("120 OPEN #12:NAME$,RELATIVE 50,INPUT,FIXED,INTERNAL", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            20 to "  TI BASIC READY",
            22 to " >120 OPEN #12:NAME$,RELATIVE",
            23 to "  50,INPUT,FIXED,INTERNAL",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testOpenCs1ForOutputWithDefaults() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll("100 OPEN #10:\"CS1\",OUTPUT,FIXED", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            20 to "  TI BASIC READY",
            22 to " >100 OPEN #10:\"CS1\",OUTPUT,FI",
            23 to "  XED",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testOpenWithDefaultModeUpdate() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll("100 OPEN #53:NAME$,FIXED,INTERNAL,RELATIVE", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            20 to "  TI BASIC READY",
            22 to " >100 OPEN #53:NAME$,FIXED,INT",
            23 to "  ERNAL,RELATIVE",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testOpenWithVariableRecordTypeAndGivenRecordLength() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll("100 OPEN #11:NAME$,INPUT,INTERNAL,SEQUENTIAL,VARIABLE 100", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            19 to "  TI BASIC READY",
            21 to " >100 OPEN #11:NAME$,INPUT,INT",
            22 to "  ERNAL,SEQUENTIAL,VARIABLE 10",
            23 to "  0",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testRunOpenAndCloseCs1() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 OPEN #2:"CS1",INTERNAL,INPUT,FIXED
         290 CLOSE #2
         300 END
         RUN
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            3 to "  TI BASIC READY",
            5 to " >100 OPEN #2:\"CS1\",INTERNAL,I",
            6 to "  NPUT,FIXED",
            7 to " >290 CLOSE #2",
            8 to " >300 END",
            9 to " >RUN",
            12 to "  * REWIND CASSETTE TAPE   CS1",
            13 to "    THEN PRESS ENTER",
            15 to "  * PRESS CASSETTE PLAY    CS1",
            16 to "    THEN PRESS ENTER",
            19 to "  * PRESS CASSETTE STOP    CS1",
            20 to "    THEN PRESS ENTER",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testCloseWithAndWithoutDeleteOption() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 OPEN #6:"CS1",SEQUENTIAL,INTERNAL,INPUT,FIXED
         110 OPEN #25:"CS2",SEQUENTIAL,INTERNAL,OUTPUT,FIXED
         200 CLOSE #6:DELETE
         210 CLOSE #25
         220 END
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            15 to "  TI BASIC READY",
            17 to " >100 OPEN #6:\"CS1\",SEQUENTIAL",
            18 to "  ,INTERNAL,INPUT,FIXED",
            19 to " >110 OPEN #25:\"CS2\",SEQUENTIA",
            20 to "  L,INTERNAL,OUTPUT,FIXED",
            21 to " >200 CLOSE #6:DELETE",
            22 to " >210 CLOSE #25",
            23 to " >220 END",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testCloseWithCassetteRecorderInstructions() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 OPEN #24:"CS1",INTERNAL,INPUT,FIXED
         110 OPEN #19:"CS2",INTERNAL,OUTPUT,FIXED
         200 CLOSE #24
         210 CLOSE #19
         220 END
         RUN
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            1 to "  * REWIND CASSETTE TAPE   CS1",
            2 to "    THEN PRESS ENTER",
            4 to "  * PRESS CASSETTE PLAY    CS1",
            5 to "    THEN PRESS ENTER",
            8 to "  * REWIND CASSETTE TAPE   CS2",
            9 to "    THEN PRESS ENTER",
            11 to "  * PRESS CASSETTE RECORD  CS2",
            12 to "    THEN PRESS ENTER",
            15 to "  * PRESS CASSETTE STOP    CS1",
            16 to "    THEN PRESS ENTER",
            19 to "  * PRESS CASSETTE STOP    CS2",
            20 to "    THEN PRESS ENTER",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testInputNumericAndStringVariables() {
      val machine = TiBasicModule()
      machine.attachCassetteTape("CS1", "1,2,\"3\",\"FOUR\",5,6,\"7\",99,99,\"99\",\"99\",99,99,\"99\"")

      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 OPEN #13:"CS1",SEQUENTIAL,DISPLAY,INPUT,FIXED
         110 INPUT #13:A,B,C$,D$,X,Y,Z$
         120 IF A=99 THEN 150
         130 PRINT A;B:C$:D$:X;Y:Z$
         140 GOTO 110
         150 CLOSE #13
         160 END
         RUN
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            1 to " >140 GOTO 110",
            2 to " >150 CLOSE #13",
            3 to " >160 END",
            4 to " >RUN",
            7 to "  * REWIND CASSETTE TAPE   CS1",
            8 to "    THEN PRESS ENTER",
            10 to "  * PRESS CASSETTE PLAY    CS1",
            11 to "    THEN PRESS ENTER",
            12 to "   1  2",
            13 to "  3",
            14 to "  FOUR",
            15 to "   5  6",
            16 to "  7",
            19 to "  * PRESS CASSETTE STOP    CS1",
            20 to "    THEN PRESS ENTER",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testInputNumericVariablesWithSingleFixedRecordTest() {
      val machine = TiBasicModule()
      machine.attachCassetteTape("CS1", "22,77,56,92")

      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 OPEN #13:"CS1",SEQUENTIAL,DISPLAY,INPUT,FIXED 64
         110 INPUT #13:A,B,C,D
         290 CLOSE #13
         300 END
         RUN
         """.trimIndent(), machine
      )

      assertEquals(22.0, machine.getNumericVariableValue("A").toNative())
      assertEquals(77.0, machine.getNumericVariableValue("B").toNative())
      assertEquals(56.0, machine.getNumericVariableValue("C").toNative())
      assertEquals(92.0, machine.getNumericVariableValue("D").toNative())
   }

   @Test
   fun testInputNumericVariablesWithMultipleFixedRecordsTest() {
      val machine = TiBasicModule()
      machine.attachCassetteTape("CS1", "22,33.5,405,92,-22,11023,99,100")

      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 OPEN #13:"CS1",SEQUENTIAL,DISPLAY,INPUT,FIXED 64
         110 INPUT #13:A,B,C,D,E,F,G
         400 END
         RUN
         """.trimIndent(), machine
      )
      assertEquals(22.0, machine.getNumericVariableValue("A").toNative())
      assertEquals(33.5, machine.getNumericVariableValue("B").toNative())
      assertEquals(405.0, machine.getNumericVariableValue("C").toNative())
      assertEquals(92.0, machine.getNumericVariableValue("D").toNative())
      assertEquals(-22.0, machine.getNumericVariableValue("E").toNative())
      assertEquals(11023.0, machine.getNumericVariableValue("F").toNative())
      assertEquals(99.0, machine.getNumericVariableValue("G").toNative())
   }

   @Test
   fun testDisplayInputWithoutAndWithInputError() {
      val machine = TiBasicModule()
      machine.attachCassetteTape("CS1", "22,97.6,TEXAS,\"AUTO LICENSE \",22000,-.07,JG,22,TEXAS,PROPERTY TAX,42,15")

      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 OPEN #13:"CS1",SEQUENTIAL,DISPLAY,INPUT,FIXED 64
         110 INPUT #13:A,B,STATE$,D$,X,Y
         120 INPUT #13:A,B,STATE$,D$,X,Y
         RUN
         """.trimIndent(), machine
      )
      // Values should match first tape record, where the types match
      assertEquals(22.0, machine.getNumericVariableValue("A").toNative())
      assertEquals(97.6, machine.getNumericVariableValue("B").toNative())
      assertEquals("TEXAS", machine.getStringVariableValue("STATE$").toNative())
      assertEquals("AUTO LICENSE ", machine.getStringVariableValue("D$").toNative())
      assertEquals(22000.0, machine.getNumericVariableValue("X").toNative())
      assertEquals(-.07, machine.getNumericVariableValue("Y").toNative())
   }

   @Test
   fun testInputInternalRelative() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 OPEN #4:NAME$,RELATIVE,INTERNAL,INPUT,FIXED 64
         110 INPUT #4:A,B,C$,D$,X
         200 CLOSE #4
         210 END
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            17 to "  TI BASIC READY",
            19 to " >100 OPEN #4:NAME$,RELATIVE,I",
            20 to "  NTERNAL,INPUT,FIXED 64",
            21 to " >110 INPUT #4:A,B,C$,D$,X",
            22 to " >200 CLOSE #4",
            23 to " >210 END",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testUpdateInternalRelativeRecClause() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 OPEN #6:NAME$,RELATIVE,INTERNAL,UPDATE,FIXED 72
         110 INPUT K
         120 INPUT #6,REC K:A,B,C$,D$
         170 PRINT #6,REC K:A,B,C$,D$
         300 CLOSE #6
         310 END
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            13 to "  TI BASIC READY",
            15 to " >100 OPEN #6:NAME$,RELATIVE,I",
            16 to "  NTERNAL,UPDATE,FIXED 72",
            17 to " >110 INPUT K",
            18 to " >120 INPUT #6,REC K:A,B,C$,D$",
            20 to " >170 PRINT #6,REC K:A,B,C$,D$",
            22 to " >300 CLOSE #6",
            23 to " >310 END",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testUpdateFileWithoutRecClause() {
      val records = listOf(
         StringConstant("1A"), StringConstant("1B"), StringConstant("1C"), NumericConstant(11), NumericConstant(12),
         StringConstant("2A"), StringConstant("2B"), StringConstant("2C"), NumericConstant(21), NumericConstant(22),
         StringConstant("3A"), StringConstant("3B"), StringConstant("3C"), NumericConstant(31), NumericConstant(32),
         StringConstant("4A"), StringConstant("4B"), StringConstant("4C"), NumericConstant(41), NumericConstant(42),
         StringConstant("5A"), StringConstant("5B"), StringConstant("5C"), NumericConstant(51), NumericConstant(52),
         StringConstant("6A"), StringConstant("6B"), StringConstant("6C"), NumericConstant(61), NumericConstant(62),
         StringConstant("7A"), StringConstant("7B"), StringConstant("7C"), NumericConstant(71), NumericConstant(72),
         StringConstant("8A"), StringConstant("8B"), StringConstant("8C"), NumericConstant(81), NumericConstant(82)
      )
      val numRecords = records.size / 5 // Used instead of 10 in example in order to reduce test overhead

      val machine = TiBasicModule()
      machine.attachDiskDrive(1, listOf(TiDiskDriveFile("FILE", TiFileContentsBytes.create(records))))

      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("10 NAME$=\"DSK1.FILE\"", machine) // Defined in order to make example run without error
      interpreter.interpretAll(
         """
         100 OPEN #3:NAME$,RELATIVE,INTERNAL,UPDATE,FIXED
         110 FOR I=1 TO ${numRecords / 2}
         120 INPUT #3:A$,B$,C$,X,Y
         230 PRINT #3:A$,B$,C$,X,Y
         240 NEXT I
         250 CLOSE #3
         260 END
         RUN
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            9 to "  TI BASIC READY",
            11 to " >10 NAME$=\"DSK1.FILE\"",
            12 to " >100 OPEN #3:NAME$,RELATIVE,I",
            13 to "  NTERNAL,UPDATE,FIXED",
            14 to " >110 FOR I=1 TO ${numRecords / 2}",
            15 to " >120 INPUT #3:A$,B$,C$,X,Y",
            16 to " >230 PRINT #3:A$,B$,C$,X,Y",
            17 to " >240 NEXT I",
            18 to " >250 CLOSE #3",
            19 to " >260 END",
            20 to " >RUN",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )

      assertNotEquals(NumericConstant.ZERO, machine.isEndOfFile(NumericConstant(3)),
         "EOF must indicate that all $numRecords records have been read")
   }

   @Test
   fun testPendingInputWithFileNumberZero() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
         100 INPUT #0:A,B,
         110 PRINT A;B
         120 GOTO 100
         RUN
         """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            14 to "  TI BASIC READY",
            16 to " >100 INPUT #0:A,B,",
            17 to " >110 PRINT A;B",
            18 to " >120 GOTO 100",
            19 to " >RUN",
            20 to "  ?",
            21 to "  * INCORRECT STATEMENT",
            22 to "     IN 100",
            24 to " >"
         ), machine.screen
      )
   }

}