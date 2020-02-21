package com.github.mmrsic.ti99.basic

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.parser.ParseException
import com.github.mmrsic.ti99.basic.betterparse.TiBasicParser
import com.github.mmrsic.ti99.basic.expr.*
import com.github.mmrsic.ti99.hw.CodeSequenceProvider
import com.github.mmrsic.ti99.hw.TiBasicModule
import com.github.mmrsic.ti99.hw.TiFctnCode
import com.github.mmrsic.ti99.hw.checkLineNumber
import java.util.*

/**
 * A TI Basic interpreter for a given [TiBasicModule].
 */
abstract class TiBasicInterpreter(val machine: TiBasicModule)

class TiBasicCommandLineInterpreter(machine: TiBasicModule) : TiBasicInterpreter(machine) {
    private val parser = TiBasicParser(machine)

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
        } catch (e: CantDoThat) {
            screen.scroll()
            screen.print("* ${e.message}")
            screen.scroll()
        } catch (e: BadName) {
            screen.print("* ${e.message}")
            screen.scroll()
        } catch (e: NumberTooBig) {
            e.displayOn(screen)
        } catch (e: CantContinue) {
            screen.print("* ${e.message}")
            screen.scroll()
        }
        if (parseResult is NewCommand) {
            machine.initCommandScreen()
        } else {
            screen.acceptAt(24, 2, ">")
        }
        return
    }

    fun interpretAll(inputLines: List<String>, machine: TiBasicModule) {
        for (inputLine in inputLines) {
            interpret(inputLine, machine)
        }
    }

    fun interpretAll(inputLines: String, machine: TiBasicModule) {
        val inputList = inputLines.split("\n")
        interpretAll(inputList, machine)
    }

}

/** Interpreter for TI Basic programs. */
class TiBasicProgramInterpreter(machine: TiBasicModule, private val codeSequenceProvider: CodeSequenceProvider) :
    TiBasicInterpreter(machine) {

    /** Interpret the current program of this interpreter's [machine], optionally starting at a given start line number. */
    fun interpretAll(startLineNum: Int? = null) {
        val program = machine.program ?: throw CantDoThat()
        println("Executing $program")
        var pc: Int? = startLineNum ?: program.firstLineNumber()
        while (pc != null && machine.programInterpreter != null) {
            if (machine.traceProgramExecution) {
                PrintStatement(listOf(StringConstant("<$pc>"), PrintToken.Adjacent)).execute(machine, pc)
            }
            if (machine.hasBreakpoint(pc)) throw TiBasicProgramException(pc, Breakpoint())
            val stmt = program.getStatements(pc)[0]
            println("Executing $pc $stmt")
            try {
                stmt.execute(machine, pc)
            } catch (e: TiBasicException) {
                throw TiBasicProgramException(pc, e)
            }
            if (jumpToLineNumber != null) {
                checkLineNumber(jumpToLineNumber!!)
                if (!program.hasLineNumber(jumpToLineNumber!!)) throw BadLineNumber()
            }
            pc = jumpToLineNumber ?: program.nextLineNumber(pc)
            jumpToLineNumber = null
        }
    }

    /** Begin a new for-loop. */
    fun beginForLoop(lineNumber: Int, varName: String, limit: NumericConstant, givenIncrement: NumericConstant?) {
        val program = machine.program ?: throw IllegalArgumentException("Can't begin for-loop without program")
        val increment = givenIncrement ?: NumericConstant(1)
        if (increment.isZero()) throw BadValue()
        val jumpLineNumber = program.nextLineNumber(lineNumber)
            ?: throw IllegalArgumentException("Line number has no successor: $lineNumber")
        val loop = ForLoop(jumpLineNumber, varName, limit, increment)
        forLoopStack.push(loop)
        println("Started: $loop")
        if (!loop.checkCtrlVariableValue(machine.getNumericVariableValue(varName))) {
            jumpTo(program.findLineWithStatement(loop.startLineNumber) { stmt ->
                stmt is NextStatement && stmt.ctrlVarName == loop.varName
            }!!)
        }
    }

    /** Start the next for-loop step, or end the loop. */
    fun nextForLoopStep(varName: String) {
        val loop = forLoopStack.peek()
        val nestedLoopError = forLoopError[loop]
        if (nestedLoopError != null) throw nestedLoopError
        if (loop.varName != varName) throw IllegalArgumentException("Expected: ${loop.varName}, got $varName")
        val oldConst = machine.getNumericVariableValue(loop.varName)
        val newConst = machine.setNumericVariable(loop.varName, Addition(oldConst, loop.increment))
        if (loop.checkCtrlVariableValue(newConst)) {
            jumpTo(loop.startLineNumber)
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
        val program = machine.program ?: throw IllegalArgumentException("Can't execute GOSUB without program")
        val gosub = Gosub(subLineNumber, program.nextLineNumber(currLineNumber))
        gosubStack.push(gosub)
        jumpTo(gosub.subprogramLineNumber)
        println("Called $gosub")
    }

    /** Return from the currently executing subprogram. */
    fun returnFromGosub() {
        val gosub = gosubStack.pop()
        if (gosub.returnLineNumber != null) jumpTo(gosub.returnLineNumber) else machine.endProgramRun()
        println("Returned from $gosub")
    }

    /** Unconditional jump, that is, GO TO a given program line number. */
    fun jumpTo(existingProgramLineNumber: Int) {
        checkLineNumber(existingProgramLineNumber)
        jumpToLineNumber = existingProgramLineNumber
        println("Set jump to program line number: $jumpToLineNumber")
    }

    /** Accept user input from [codeSequenceProvider] into a given variable. */
    fun acceptUserInput(variableNames: List<Expression>, inputLineNumber: Int, prompt: String): String {
        val inputEndingChars = listOf(TiFctnCode.Enter.toChar()) // TODO: Add character codes for navigation keys
        acceptUserInputCtx.addCall(inputLineNumber, prompt)
        val input = StringBuilder().apply {
            do {
                val inputPart = codeSequenceProvider.provideInput(acceptUserInputCtx)
                if (inputPart.contains(TiFctnCode.Clear.toChar())) throw Breakpoint()
                for (char in inputPart.takeWhile { it !in inputEndingChars }) append(char)
                if (length > 10000) {
                    throw IllegalArgumentException("Endless loop detected")
                }
            } while (inputPart.none { it in inputEndingChars })
        }.toString()
        machine.printTokens(listOf(StringConstant(input), PrintToken.Adjacent))
        val inputParts = parseInputParts(input)
        variableNames.forEachIndexed { varIdx, varNameExpr ->
            val trimmedPart = inputParts[varIdx].trim()
            val unquotedPart = if (trimmedPart.first() == '"') trimmedPart.drop(1).dropLast(1) else trimmedPart
            val memoryVarName = when (varNameExpr) {
                is NumericVariable -> varNameExpr.name
                is StringVariable -> varNameExpr.name
                is NumericArrayAccess -> machine.getArrayVariableName(varNameExpr.baseName, varNameExpr.arrayIndex)
                else -> throw IllegalArgumentException("Unknown variable expression: $varNameExpr")
            }
            machine.setVariable(memoryVarName, unquotedPart.replace("\"\"", "\""))
        }
        return input
    }


    // HELPERS //

    private val forLoopStack: Stack<ForLoop> = Stack()
    private val forLoopError: MutableMap<ForLoop, TiBasicError> = mutableMapOf()
    private val gosubStack: Stack<Gosub> = Stack()
    private var jumpToLineNumber: Int? = null

    private data class ForLoop(
        val startLineNumber: Int,
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

    private val acceptUserInputCtx = object : CodeSequenceProvider.Context {
        private val allCalls = mutableMapOf<Int, Int>()
        override var prompt: String = ""
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
            unacceptedInputs = 0
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

}
