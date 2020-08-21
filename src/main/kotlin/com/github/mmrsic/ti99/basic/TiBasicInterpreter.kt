package com.github.mmrsic.ti99.basic

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.parser.ParseException
import com.github.mmrsic.ti99.basic.betterparse.TiBasicParser
import com.github.mmrsic.ti99.basic.expr.Addition
import com.github.mmrsic.ti99.basic.expr.Constant
import com.github.mmrsic.ti99.basic.expr.NumericConstant
import com.github.mmrsic.ti99.basic.expr.PrintSeparator
import com.github.mmrsic.ti99.basic.expr.StringConstant
import com.github.mmrsic.ti99.hw.KeyboardInputProvider
import com.github.mmrsic.ti99.hw.TiBasicModule
import com.github.mmrsic.ti99.hw.TiColor
import com.github.mmrsic.ti99.hw.TiFctnCode
import com.github.mmrsic.ti99.hw.TiPlainCode
import com.github.mmrsic.ti99.hw.Variable
import com.github.mmrsic.ti99.hw.checkLineNumber
import java.util.Stack
import java.util.TreeMap

/**
 * A TI Basic interpreter for a given [TiBasicModule].
 * @param machine the TI Basic module this interpreter is operating on
 */
abstract class TiBasicInterpreter(protected val machine: TiBasicModule)

/** A [TiBasicInterpreter] for the command line of a given [machine]. */
class TiBasicCommandLineInterpreter(machine: TiBasicModule) : TiBasicInterpreter(machine) {

   private val parser = TiBasicParser(machine)

   /** Interpret a given [inputLine] as a single command. */
   fun interpret(inputLine: String, machine: TiBasicModule) {
      val screen = machine.screen
      screen.print(inputLine)
      if (inputLine.length == 28) machine.screen.scroll()
      println("Parsing command line input: $inputLine")
      val parseResult = try {
         parser.parseToEnd(inputLine)
      } catch (e: ParseException) {
         println("Parse error: ${e.errorResult}")
      }
      if (parseResult !is TiBasicExecutable) {
         IncorrectStatement().displayOn(screen)
         screen.acceptAt(24, 2, ">")
         return
      }

      println("Executing $parseResult")
      try {
         parseResult.execute(machine)
         if (parseResult.requiresEmptyLineAfterExecution()) screen.scroll()
      } catch (e: BadLineNumber) {
         if (!setOf(RunCommand::class, ResequenceCommand::class).contains(parseResult::class)) {
            screen.scroll()
         }
         screen.print("* ${e.message}")
         screen.scroll()
      } catch (e: TiBasicException) {
         e.displayOn(screen)
      }
      if (parseResult is NewCommand) {
         machine.initCommandScreen()
      } else {
         screen.acceptAt(24, 2, ">")
      }
      return
   }

   /** Interpret given [inputLines] as a list of commands. */
   fun interpretAll(inputLines: List<String>, machine: TiBasicModule) {
      for (inputLine in inputLines) {
         interpret(inputLine, machine)
      }
   }

   /** Interpret given [inputLines] as a list of commands separated with \n. */
   fun interpretAll(inputLines: String, machine: TiBasicModule) {
      val inputList = inputLines.split("\n")
      interpretAll(inputList, machine)
   }

}

/** Interpreter for TI Basic programs. */
class TiBasicProgramInterpreter(machine: TiBasicModule, programData: Map<Int, List<Constant>>) : TiBasicInterpreter(machine) {

   companion object {
      /** Background color used while interpreting a program. */
      val RUN_BACKGROUND_COLOR = TiColor.LightGreen
   }

   /** Interpret the current program of this interpreter's [machine], optionally starting at a given start line number. */
   fun interpretAll(startLineNum: Int? = null) {
      val program = machine.program ?: throw CantDoThat()
      machine.colors.background = RUN_BACKGROUND_COLOR
      println("Executing $program")
      var pc: Int? = startLineNum ?: program.firstLineNumber()
      while (pc != null && machine.programInterpreter != null) {
         if (machine.traceProgramExecution) {
            PrintStatement(listOf(StringConstant("<$pc>"), PrintSeparator.Adjacent)).execute(machine, pc)
         }
         if (machine.hasBreakpoint(pc)) throw TiBasicProgramException(pc, Breakpoint())
         val stmt = program.getStatements(pc)[0]
         println("Executing $pc $stmt")
         try {
            stmt.execute(machine, pc)
         } catch (e: TiBasicWarning) {
            TiBasicProgramException(pc, e).displayOn(machine.screen)
         } catch (e: TiBasicException) {
            throw TiBasicProgramException(pc, e)
         }
         if (jumpToLineNumber != null) {
            checkLineNumber(jumpToLineNumber!!)
            if (!program.hasLineNumber(jumpToLineNumber!!)) throw TiBasicProgramException(pc, BadLineNumber())
         }
         machine.programLineExecutionComplete(pc)
         pc = jumpToLineNumber ?: program.nextLineNumber(pc)
         jumpToLineNumber = null
      }
   }

   // TODO: Use program line hooks instead of For-Loop-Stack?

   /** Begin a new for-loop. */
   fun beginForLoop(lineNumber: Int, varName: String, limit: NumericConstant, givenIncrement: NumericConstant?) {
      val program = machine.program ?: throw IllegalArgumentException("Can't begin for-loop without program")
      if (forLoopStack.isNotEmpty() && forLoopStack.peek().forLineNumber == lineNumber) {
         val removedElem = forLoopStack.pop()
         println("Removed: $removedElem")
      }
      val increment = givenIncrement ?: NumericConstant.ONE
      if (increment.isZero()) throw BadValue()
      val jumpLineNumber = program.nextLineNumber(lineNumber)
         ?: throw IllegalArgumentException("Line number has no successor: $lineNumber")
      val loop = ForLoop(lineNumber, jumpLineNumber, varName, limit, increment)
      forLoopStack.push(loop)
      println("Started: $loop")
      if (!loop.checkCtrlVariableValue(machine.getNumericVariableValue(varName))) {
         jumpTo(program.findLineWithStatement(loop.forLineNumber) { stmt ->
            stmt is NextStatement && stmt.ctrlVarName == loop.varName
         }!!)
      }
   }

   /** Start the next for-loop step, or end the loop. */
   fun nextForLoopStep(varName: String) {
      if (forLoopStack.isEmpty()) throw ForNextError()
      val loop = forLoopStack.peek()
      val nestedLoopError = forLoopError[loop]
      if (nestedLoopError != null) throw nestedLoopError
      if (loop.varName != varName) throw IllegalArgumentException("Expected loop variable '${loop.varName}', got $varName")
      val oldConst = machine.getNumericVariableValue(loop.varName)
      val newConst = machine.setNumericVariable(loop.varName, Addition(oldConst, loop.increment))
      if (loop.checkCtrlVariableValue(newConst)) {
         jumpTo(loop.jumpLineNumber)
      } else {
         val oldElem = forLoopStack.pop()
         forLoopStack.filter { stackElem -> stackElem.varName == oldElem.varName }.forEach {
            forLoopError[it] = CantDoThat()
         }
         println("Ended: $loop")
      }
   }

   /** GO to a subprogram given by its line number. */
   fun gosub(subLineNumber: Int, currLineNumber: Int) {
      if (subLineNumber == currLineNumber) throw MemoryFull()
      val program = machine.program ?: throw IllegalArgumentException("Can't execute GOSUB without program")
      val gosub = Gosub(subLineNumber, program.nextLineNumber(currLineNumber))
      gosubStack.push(gosub)
      jumpTo(gosub.subprogramLineNumber)
      println("Called $gosub")
   }

   /** Return from the currently executing subprogram. */
   fun returnFromGosub() {
      val gosub = gosubStack.pop()
      if (gosub.returnLineNumber == null) throw CantDoThat()
      jumpTo(gosub.returnLineNumber)
      println("Returned from $gosub")
   }

   /** Unconditional jump, that is, GO TO a given program line number. */
   fun jumpTo(existingProgramLineNumber: Int) {
      checkLineNumber(existingProgramLineNumber)
      jumpToLineNumber = existingProgramLineNumber
      println("Set jump to program line number: $jumpToLineNumber")
   }

   /** Accept user input from [keyboardInputProvider] into a given variable. */
   fun acceptUserInput(variableNames: List<Variable>, inputLineNumber: Int, prompt: String,
                       keyboardInputProvider: KeyboardInputProvider): String {
      val inputEndingChars = listOf(TiPlainCode.Enter.toChar()) // TODO: Add character codes for navigation keys
      acceptUserInputCtx.addCall(inputLineNumber, prompt)
      machine.printTokens(listOf(StringConstant(acceptUserInputCtx.prompt), PrintSeparator.Adjacent))
      val input = StringBuilder().apply {
         do {
            val inputPart = keyboardInputProvider.provideInput(acceptUserInputCtx)
            if (inputPart.contains(TiFctnCode.Clear.toChar())) throw Breakpoint()
            for (char in inputPart.takeWhile { it !in inputEndingChars }) append(char)
            if (length > 10000) throw IllegalArgumentException("Endless loop detected")
            machine.printTokens(listOf(StringConstant(toString()), PrintSeparator.Adjacent))
         } while (inputPart.none { it in inputEndingChars })
      }.toString()
      val inputParts = parseInputParts(input)
      if (inputParts.size != variableNames.size) {
         println("Input error: Expecting ${variableNames.size} elements but got ${inputParts.size}")
         raiseInputErrorWarning(inputLineNumber, InputWarning())
      }
      variableNames.forEachIndexed { varIdx, varNameExpr ->
         val trimmedPart = inputParts[varIdx].trim()
         val unquotedPart = if (trimmedPart.isNotEmpty() && trimmedPart.first() == '"')
            trimmedPart.drop(1).dropLast(1)
         else
            trimmedPart
         try {
            machine.setVariable(varNameExpr, unquotedPart.replace("\"\"", "\""))
         } catch (e: NumberFormatException) {
            println("Input warning: Expecting number but got string: ${e.message}")
            raiseInputErrorWarning(inputLineNumber, InputWarning())
         } catch (e: TiBasicWarning) {
            println("Input warning: ${e.message}")
            raiseInputErrorWarning(inputLineNumber, e)
         }
      }
      return input
   }

   // HELPERS //

   /** Data for a program. */
   private val programData = object {
      val constants: List<Constant>

      /** Mapping from program line to [constants] index. */
      private val restoreEntryPoints: TreeMap<Int, Int>

      init {
         val sortedProgramData = TreeMap(programData)
         constants = sortedProgramData.flatMap { it.value }
         restoreEntryPoints = TreeMap()
         var currOffset = 0
         for ((line, constants) in sortedProgramData) {
            restoreEntryPoints[line] = currOffset
            currOffset += constants.size
         }
      }

      private var nextIndex = 0
      fun next(): Constant {
         if (nextIndex >= constants.size) throw DataError()
         return constants[nextIndex++]
      }

      /** Restore the [readData] pointer, optionally to a given line number. */
      fun restore(lineNumber: Int? = null) {
         nextIndex = when {
            lineNumber == null -> 0
            restoreEntryPoints.containsKey(lineNumber) -> restoreEntryPoints.getValue(lineNumber)
            else -> {
               val nextDataLineNumber = restoreEntryPoints.higherKey(lineNumber)
               if (nextDataLineNumber != null) {
                  restoreEntryPoints.getValue(nextDataLineNumber)
               } else {
                  constants.size // That is, throw a data error if READ follows
               }
            }
         }
      }
   }

   /** Access some program data stored with [TiBasicModule.storeData]. */
   fun readData(variableNames: List<Variable>) {
      for (varNameExpr in variableNames) {
         val varValue = programData.next().constant.toString()
         try {
            machine.setVariable(varNameExpr, varValue)
         } catch (e: java.lang.NumberFormatException) {
            println("Failed to read numeric value from data entry: $varValue")
            throw DataError()
         }
      }
   }

   /**
    * Restore the data stored with [TiBasicModule.storeData], that is, the next [readData] starts from the beginning.
    */
   fun restore(lineNumber: Int? = null) = programData.restore(lineNumber)

   private val forLoopStack: Stack<ForLoop> = Stack()
   private val forLoopError: MutableMap<ForLoop, TiBasicError> = mutableMapOf()
   private val gosubStack: Stack<Gosub> = Stack()
   private var jumpToLineNumber: Int? = null

   /** A FOR-LOOP statement. */
   private data class ForLoop(
      /** Start line of the loop, that is the program line number with the FOR statement*/
      val forLineNumber: Int,
      val jumpLineNumber: Int,
      val varName: String,
      val limit: NumericConstant,
      val increment: NumericConstant
   ) {

      /** Check whether a given value of the control variable is within the loop range. */
      fun checkCtrlVariableValue(ctrlVariableValue: NumericConstant): Boolean {
         val newValue = ctrlVariableValue.toNative()
         val limitValue = limit.toNative()
         val increasing = increment.toNative() > 0
         return if (increasing) newValue <= limitValue else newValue >= limitValue
      }
   }

   private data class Gosub(val subprogramLineNumber: Int, val returnLineNumber: Int?)

   private val acceptUserInputCtx = object : KeyboardInputProvider.InputContext {
      private val allCalls = mutableMapOf<Int, Int>()
      override var prompt: String = ""
         get() = if (unacceptedInputs > 0) "TRY AGAIN: " else field
      override val overallCalls: Int
         get() = if (allCalls.isEmpty()) 0 else allCalls.values.reduce(Int::plus)
      override var programLine: Int = 0
      override val programLineCalls: Int
         get() = allCalls[programLine] ?: 0
      override var unacceptedInputs: Int = 0

      fun addCall(lineNumber: Int, userPrompt: String = "? ") {
         programLine = lineNumber
         prompt = userPrompt
         val oldValue = allCalls[lineNumber] ?: 0
         allCalls[lineNumber] = oldValue + 1
      }
   }

   private fun parseInputParts(input: String): List<String> {
      val commaPositions = mutableListOf<Int>()
      val status = mutableListOf("nothing")
      input.forEachIndexed { charPos, char ->
         when (char) {
            ',' -> if (status.last() == "nothing") commaPositions.add(charPos)
            '"' -> when (status.last()) {
               "nothing" -> status.add("string")
               "string" -> status.add("stringEnd")
               "stringEnd" -> status.remove("stringEnd")
               else -> status.remove("string")
            }
         }
      }
      val result = mutableListOf<String>()
      val itr = commaPositions.listIterator()
      while (itr.hasNext()) {
         val start = if (itr.hasPrevious()) itr.previous() + 1 else 0
         if (start > 0) itr.next()
         val end = itr.next()
         result.add(input.substring(start, end))
      }
      val start = if (commaPositions.isEmpty()) 0 else commaPositions.last() + 1
      result.add(input.substring(start, input.length))
      return result.toList()
   }

   private fun raiseInputErrorWarning(inputLineNumber: Int, exception: TiBasicException) {
      acceptUserInputCtx.unacceptedInputs++
      machine.printTokens(listOf(PrintSeparator.NextRecord))
      jumpTo(inputLineNumber)
      throw exception
   }

}
