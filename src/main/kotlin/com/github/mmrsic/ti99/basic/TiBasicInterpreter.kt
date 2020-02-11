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
    fun beginForLoop(lineNumber: Int, stmt: LetNumberStatement, limit: NumericExpr, increment: Double = 1.0) {
        val program = machine.program ?: throw IllegalArgumentException("Can't begin for-loop without program")
        if (increment == 0.0) throw BadValue()
        val jumpLineNumber = program.nextLineNumber(lineNumber)
            ?: throw IllegalArgumentException("Line number has no successor: $lineNumber")
        stmt.execute(machine)
        val varName = stmt.varName
        val currValue = machine.getNumericVariableValue(varName)
        val loop = ForLoop(jumpLineNumber, varName, currValue.toNative()..limit.value().toNative(), increment)
        forLoopStack.push(loop)
        println("Started: $loop")
    }

    /** Start the next for-loop step, or end the loop. */
    fun nextForLoopStep(varName: String) {
        // TODO: Check variable name
        val loop = forLoopStack.peek()
        val currValue: NumericConstant = machine.getNumericVariableValue(loop.varName)
        val newValue: NumericConstant = Addition(currValue, loop.increment).value()
        machine.setNumericVariable(loop.varName, newValue)
        if (newValue.toNative() in loop.continueRange) {
            jumpTo(loop.startLineNumber)
            return
        }
        forLoopStack.pop()
        println("Ended: $loop")
    }

    /** Unconditional jump, that is, GO TO a given program line number. */
    fun jumpTo(existingProgramLineNumber: Int) {
        checkLineNumber(existingProgramLineNumber)
        jumpToLineNumber = existingProgramLineNumber
        println("Set jump to program line number: $jumpToLineNumber")
    }

    /** Accept user input from [codeSequenceProvider] into a given variable. */
    fun acceptUserInput(variableName: String, programLineNumber: Int): String {
        val inputEndingChars = listOf(TiFctnCode.Enter.toChar()) // TODO: Add character codes for navigation keys
        acceptUserInputCtx.addCall(programLineNumber)
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
        machine.setVariable(variableName, input)
        return input
    }


    // HELPERS //

    private val forLoopStack: Stack<ForLoop> = Stack()
    private var jumpToLineNumber: Int? = null

    private class ForLoop(
        val startLineNumber: Int,
        val varName: String,
        val continueRange: ClosedRange<Double>,
        increment: Number
    ) {
        val increment = NumericConstant(increment)
    }

    private val acceptUserInputCtx = object : CodeSequenceProvider.Context {
        private val allCalls = mutableMapOf<Int, Int>()
        override val overallCalls: Int
            get() = if (allCalls.isEmpty()) 0 else allCalls.values.reduce(Int::plus)
        override var programLine: Int = 0
        override val programLineCalls: Int
            get() = allCalls[programLine] ?: 0
        override var unacceptedInputs: Int = 0

        fun addCall(lineNumber: Int) {
            programLine = lineNumber
            val oldValue = allCalls[lineNumber] ?: 0
            allCalls[lineNumber] = oldValue + 1
            unacceptedInputs = 0
        }
    }

}