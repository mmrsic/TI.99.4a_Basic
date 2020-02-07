package com.github.mmrsic.ti99.basic

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.parser.ParseException
import com.github.mmrsic.ti99.basic.betterparse.TiBasicParser
import com.github.mmrsic.ti99.basic.expr.*
import com.github.mmrsic.ti99.hw.TiBasicModule
import com.github.mmrsic.ti99.hw.checkLineNumber
import java.util.*

/**
 * A TI Basic interpreter for a given [TiBasicModule].
 */
abstract class TiBasicInterpreter(machine: TiBasicModule)

class TiBasicCommandLineInterpreter(machine: TiBasicModule) : TiBasicInterpreter(machine) {
    private val parser = TiBasicParser(machine)

    fun interpret(inputLine: String, machine: TiBasicModule) {
        val screen = machine.screen
        screen.print(inputLine)
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

class TiBasicProgramInterpreter(private val machine: TiBasicModule) : TiBasicInterpreter(machine) {

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
            pc = jumpToLineNumber ?: program.nextLineNumber(pc)
            jumpToLineNumber = null
        }
    }

    /** Begin a new for-loop. */
    fun beginForLoop(lineNumber: Int, stmt: LetNumberStatement, limit: NumericExpr, increment: Int = 1) {
        val program = machine.program ?: throw IllegalArgumentException("Can't begin for-loop without program")
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

}