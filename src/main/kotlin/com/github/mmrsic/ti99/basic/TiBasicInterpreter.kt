package com.github.mmrsic.ti99.basic

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.parser.ParseException
import com.github.mmrsic.ti99.basic.betterparse.TiBasicParser
import com.github.mmrsic.ti99.hw.TiBasicModule
import com.github.mmrsic.ti99.hw.TiBasicScreen

abstract class TiBasicInterpreter(machine: TiBasicModule) {

    protected fun print(e: TiBasicException, screen: TiBasicScreen, lineNumber: Int? = null) {
        // TODO? Use PrintStatement
        screen.print("")
        if (e is TiBasicWarning) {
            screen.print("* WARNING:")
            screen.print("  ${e.message}" + (if (lineNumber != null) " IN $lineNumber" else ""))
        } else {
            screen.print("* ${e.message}" + if (lineNumber != null) " IN $lineNumber" else "")
        }
        if (lineNumber == null) {
            screen.print("")
        }
    }

}

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
            screen.print("")
            screen.print("* INCORRECT STATEMENT")
            screen.print("")
            screen.acceptAt(24, 2, ">")
            return
        }

        println("Executing $parseResult")
        try {
            parseResult.execute(machine)
            if (!setOf(StoreProgramLineCommand::class, ListCommand::class, ResequenceCommand::class)
                        .contains(parseResult::class)) {
                screen.print("")
            }
        } catch (e: BadLineNumber) {
            if (!setOf(RunCommand::class, ResequenceCommand::class).contains(parseResult::class)) {
                screen.print("")
            }
            screen.print("* ${e.message}")
            screen.print("")
        } catch (e: CantDoThat) {
            screen.print("")
            screen.print("* ${e.message}")
            screen.print("")
        } catch (e: BadName) {
            screen.print("* ${e.message}")
            screen.print("")
        } catch (e: NumberTooBig) {
            print(e, screen)
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

    fun interpretAll(startLineNum: Int? = null): TiBasicError? {
        var breakReason: TiBasicError? = null
        val program = machine.program ?: throw CantDoThat()
        println("Executing $program")
        var pc: Int? = startLineNum ?: program.firstLineNumber()
        var stopRun = false
        while (pc != null && !stopRun) {
            val stmt = program.getStatements(pc)[0]
            println("Executing $pc $stmt")
            try {
                stmt.execute(machine, pc)
            } catch (e: TiBasicException) {
                print(e, machine.screen, pc)
                if (e is TiBasicError) {
                    breakReason = e
                }
            }
            pc = program.nextLineNumber(pc)
            stopRun = stmt is EndStatement
        }
        if (breakReason != null) {
            println("Broke $program: $breakReason")
        } else {
            println("Stopped $program")
        }
        return breakReason
    }

}