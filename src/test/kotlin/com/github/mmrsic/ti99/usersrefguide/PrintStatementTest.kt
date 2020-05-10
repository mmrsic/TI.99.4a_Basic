package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.TestHelperScreen
import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test

/**
 * Test cases for examples found in User's Reference Guide on pages from II-65 to II-69.
 */
class PrintStatementTest {

   @Test
   fun testNumericAndStringVariablesAndCommaIncludingStringConstant() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 A=10
            110 B=20
            120 STRING$="TI COMPUTER"
            130 PRINT A;B:STRING$
            140 PRINT "HELLO, FRIEND"
            150 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            9 to "  TI BASIC READY",
            11 to " >100 A=10",
            12 to " >110 B=20",
            13 to " >120 STRING$=\"TI COMPUTER\"",
            14 to " >130 PRINT A;B:STRING$",
            15 to " >140 PRINT \"HELLO, FRIEND\"",
            16 to " >150 END",
            17 to " >RUN",
            18 to "   10  20",
            19 to "  TI COMPUTER",
            20 to "  HELLO, FRIEND",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testStringConcatenation() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 N$="JOAN"
            110 M$="HI"
            120 PRINT M$;N$
            130 PRINT M$&" "&N$
            140 PRINT "HELLO ";N$
            150 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            9 to "  TI BASIC READY",
            11 to " >100 N$=\"JOAN\"",
            12 to " >110 M$=\"HI\"",
            13 to " >120 PRINT M$;N$",
            14 to " >130 PRINT M$&\" \"&N$",
            15 to " >140 PRINT \"HELLO \";N$",
            16 to " >150 END",
            17 to " >RUN",
            18 to "  HIJOAN",
            19 to "  HI JOAN",
            20 to "  HELLO JOAN",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testAddition() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 LET A=10.2
            110 B=-30.5
            120 C=16.7
            130 PRINT A;B;C
            140 PRINT A+B
            150 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            10 to "  TI BASIC READY",
            12 to " >100 LET A=10.2",
            13 to " >110 B=-30.5",
            14 to " >120 C=16.7",
            15 to " >130 PRINT A;B;C",
            16 to " >140 PRINT A+B",
            17 to " >150 END",
            18 to " >RUN",
            19 to "   10.2 -30.5  16.7",
            20 to "  -20.3",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testNumbersWithTenOrFewerDigitsInNormalDecimalForm() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            PRINT -10;7.1
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            19 to "  TI BASIC READY",
            21 to " >PRINT -10;7.1",
            22 to "  -10  7.1",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testNumbersWithMoreThanTenDigitsInScientificForm() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            PRINT 93427685127
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            19 to "  TI BASIC READY",
            21 to " >PRINT 93427685127",
            22 to "   9.34277E+10",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testNonIntegerNumbersInNormalOrInScientificForm() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            PRINT 1E-10
            PRINT 1.2E-10
            PRINT .000000000246
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            13 to "  TI BASIC READY",
            15 to " >PRINT 1E-10",
            16 to "   .0000000001",
            18 to " >PRINT 1.2E-10",
            19 to "   1.2E-10",
            21 to " >PRINT .000000000246",
            22 to "   2.46E-10",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testIntegers() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("PRINT 15;-3", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            19 to "  TI BASIC READY",
            21 to " >PRINT 15;-3",
            22 to "   15 -3",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testNonIntegers() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("PRINT 3.350;-46.1", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            19 to "  TI BASIC READY",
            21 to " >PRINT 3.350;-46.1",
            22 to "   3.35 -46.1",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testNonIntegerWithMoreThanTenDigits() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("PRINT 791.123456789", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            19 to "  TI BASIC READY",
            21 to " >PRINT 791.123456789",
            22 to "   791.1234568",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testNumbersWithAbsoluteValueLessThanOne() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("PRINT -12.7E-3;0.64", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            19 to "  TI BASIC READY",
            21 to " >PRINT -12.7E-3;0.64",
            22 to "  -.0127  .64",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testMantissaWithUpToSixDigitsAndAlwasOneDigitToTheLeftOfDecimalPoint() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         listOf(
            "PRINT .0000000001978531",
            "PRINT -98.77E21"
         ), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            16 to "  TI BASIC READY",
            18 to " >PRINT .0000000001978531",
            19 to "   1.97853E-10",
            21 to " >PRINT -98.77E21",
            22 to "  -9.877E+22",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testTrailingZerosAreOmmittedInFractionalPartOfMantissa() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("PRINT 736.400E10", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            19 to "  TI BASIC READY",
            21 to " >PRINT 736.400E10",
            22 to "   7.364E+12",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testFifthDigitOfMantissaIsRounded() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("PRINT 12.36587E-15", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            19 to "  TI BASIC READY",
            21 to " >PRINT 12.36587E-15",
            22 to "   1.23659E-14",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testExponentHasSignAndConsistsOfTwoDigits() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("PRINT 1.25E-9;-43.6E12", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            19 to "  TI BASIC READY",
            21 to " >PRINT 1.25E-9;-43.6E12",
            22 to "   1.25E-09 -4.36E+13",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testExponentWithMoreThanTwoDigits() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("PRINT .76E126;81E-115", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            19 to "  TI BASIC READY",
            21 to " >PRINT .76E126;81E-115",
            22 to "   7.6E+**  8.1E-**",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testTwoColonSeparators() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpret("PRINT \"A\"::\"B\"", machine)

      TestHelperScreen.assertPrintContents(
         mapOf(
            17 to "  TI BASIC READY",
            19 to " >PRINT \"A\"::\"B\"",
            20 to "  A",
            22 to "  B",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testSemicolonSeparators() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 A=-26
            110 B=-33
            120 C$="HELLO"
            130 D$="HOW ARE YOU?"
            140 PRINT A;B;C$;D$
            150 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            11 to "  TI BASIC READY",
            13 to " >100 A=-26",
            14 to " >110 B=-33",
            15 to " >120 C$=\"HELLO\"",
            16 to " >130 D$=\"HOW ARE YOU?\"",
            17 to " >140 PRINT A;B;C$;D$",
            18 to " >150 END",
            19 to " >RUN",
            20 to "  -26 -33 HELLOHOW ARE YOU?",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testColonSeparators() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 A=-26
            110 B$="HELLO"
            120 C$="HOW ARE YOU?"
            130 PRINT A:B$:C$
            140 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            10 to "  TI BASIC READY",
            12 to " >100 A=-26",
            13 to " >110 B$=\"HELLO\"",
            14 to " >120 C$=\"HOW ARE YOU?\"",
            15 to " >130 PRINT A:B$:C$",
            16 to " >140 END",
            17 to " >RUN",
            18 to "  -26",
            19 to "  HELLO",
            20 to "  HOW ARE YOU?",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testCommaSeparators() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 A$="ZONE 1"
            110 B$="ZONE 2"
            120 PRINT A$,B$
            130 PRINT A$:,B$,A$
            140 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            9 to "  TI BASIC READY",
            11 to " >100 A$=\"ZONE 1\"",
            12 to " >110 B$=\"ZONE 2\"",
            13 to " >120 PRINT A$,B$",
            14 to " >130 PRINT A$:,B$,A$",
            15 to " >140 END",
            16 to " >RUN",
            17 to "  ZONE 1        ZONE 2",
            18 to "  ZONE 1",
            19 to "                ZONE 2",
            20 to "  ZONE 1",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testTabFunctionWithLineWrapping() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 A=23.5
            110 B=48.6
            120 MSG$="HELLO"
            130 REM N>28
            140 PRINT TAB(5);MSG$;TAB(33);MSG$
            150 REM CHARACTERS ALREADY  PRINTED<=N
            160 PRINT A;TAB(10);B
            170 REM CHARACTERS ALREADY  PRINTED>N
            180 PRINT TAB(3);A;TAB(3);B
            190 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            2 to " >100 A=23.5",
            3 to " >110 B=48.6",
            4 to " >120 MSG$=\"HELLO\"",
            5 to " >130 REM N>28",
            6 to " >140 PRINT TAB(5);MSG$;TAB(33",
            7 to "  );MSG$",
            8 to " >150 REM CHARACTERS ALREADY",
            9 to "  PRINTED<=N",
            10 to " >160 PRINT A;TAB(10);B",
            11 to " >170 REM CHARACTERS ALREADY",
            12 to "  PRINTED>N",
            13 to " >180 PRINT TAB(3);A;TAB(3);B",
            14 to " >190 END",
            15 to " >RUN",
            16 to "      HELLO",
            17 to "      HELLO",
            18 to "   23.5     48.6",
            19 to "     23.5",
            20 to "     48.6",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testTabWithNumbers() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 A=326
            110 B=79
            120 PRINT A;TAB(15);B
            130 PRINT A,B
            140 PRINT A;TAB(15),B
            150 PRINT TAB(5);A;TAB(6);B
            160 PRINT A;TAB(43);B
            170 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            3 to "  TI BASIC READY",
            5 to " >100 A=326",
            6 to " >110 B=79",
            7 to " >120 PRINT A;TAB(15);B",
            8 to " >130 PRINT A,B",
            9 to " >140 PRINT A;TAB(15),B",
            10 to " >150 PRINT TAB(5);A;TAB(6);B",
            11 to " >160 PRINT A;TAB(43);B",
            12 to " >170 END",
            13 to " >RUN",
            14 to "   326           79",
            15 to "   326           79",
            16 to "   326",
            17 to "   79",
            18 to "       326",
            19 to "        79",
            20 to "   326           79",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testWrapOnlyTrailingSpaceOfNumericConst() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 A=23767
            110 B=79856
            120 C=A+B
            130 D=B-A
            140 PRINT A;B;C;D
            150 PRINT "A=";A;"B=";B;"C=";C;"D=";D
            160 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            7 to "  TI BASIC READY",
            9 to " >100 A=23767",
            10 to " >110 B=79856",
            11 to " >120 C=A+B",
            12 to " >130 D=B-A",
            13 to " >140 PRINT A;B;C;D",
            14 to " >150 PRINT \"A=\";A;\"B=\";B;\"C=\"",
            15 to "  ;C;\"D=\";D",
            16 to " >160 END",
            17 to " >RUN",
            18 to "   23767  79856  103623  56089",
            19 to "  A= 23767 B= 79856 C= 103623",
            20 to "  D= 56089",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testPrintSeparatorsAtEnd() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 A=23
            110 B=597
            120 PRINT A,
            130 PRINT B
            140 PRINT A;B;
            150 C=468
            160 PRINT C
            170 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            8 to "  TI BASIC READY",
            10 to " >100 A=23",
            11 to " >110 B=597",
            12 to " >120 PRINT A,",
            13 to " >130 PRINT B",
            14 to " >140 PRINT A;B;",
            15 to " >150 C=468",
            16 to " >160 PRINT C",
            17 to " >170 END",
            18 to " >RUN",
            19 to "   23            597",
            20 to "   23  597  468",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testNoPrintListResultsInEmptyLine() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 A=20
            110 PRINT A
            120 PRINT
            130 B=15
            140 PRINT B
            150 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            9 to "  TI BASIC READY",
            11 to " >100 A=20",
            12 to " >110 PRINT A",
            13 to " >120 PRINT",
            14 to " >130 B=15",
            15 to " >140 PRINT B",
            16 to " >150 END",
            17 to " >RUN",
            18 to "   20",
            20 to "   15",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

   @Test
   fun testNoPrintListResultsInNewLine() {
      val machine = TiBasicModule()
      val interpreter = TiBasicCommandLineInterpreter(machine)
      interpreter.interpretAll(
         """
            100 FOR J=1 TO 2
            110 FOR I=1 TO 3
            120 PRINT I;
            130 NEXT I
            140 PRINT
            150 NEXT J
            160 END
            RUN
            """.trimIndent(), machine
      )

      TestHelperScreen.assertPrintContents(
         mapOf(
            9 to "  TI BASIC READY",
            11 to " >100 FOR J=1 TO 2",
            12 to " >110 FOR I=1 TO 3",
            13 to " >120 PRINT I;",
            14 to " >130 NEXT I",
            15 to " >140 PRINT",
            16 to " >150 NEXT J",
            17 to " >160 END",
            18 to " >RUN",
            19 to "   1  2  3",
            20 to "   1  2  3",
            22 to "  ** DONE **",
            24 to " >"
         ), machine.screen
      )
   }

}