package com.github.mmrsic.ti99.basic

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.mmrsic.ti99.basic.betterparse.TiBasicParser
import com.github.mmrsic.ti99.hw.TiBasicModule

abstract class TiBasicInterpreter(machine: TiBasicModule)

class TiBasicCommandLineInterpreter(machine: TiBasicModule) : TiBasicInterpreter(machine) {
    private val parser = TiBasicParser(machine)

    fun interpret(inputLine: String, machine: TiBasicModule) {
        val screen = machine.screen
        screen.print(inputLine)
        val parseResult = parser.parseToEnd(inputLine)
        if (parseResult !is TiBasicExecutable) {
            println("Illegal command/statement: $inputLine")
            throw IncorrectStatement()
        }

        println("Executing $parseResult")
        try {
            parseResult.execute(machine)
            if (parseResult !is StoreProgramLineCommand) {
                screen.print("")
            }
        } catch (e: BadLineNumber) {
            screen.print("")
            screen.print("* ${e.message}")
            screen.print("")
        } catch (e: BadName) {
            screen.print("* ${e.message}")
            screen.print("")
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

    fun interpretAll() {
        val program = machine.program ?: throw CantDoThat()
        println("Executing $program")
        var pc: Int? = program.firstLineNumber()
        while (pc != null) {
            val stmt = program.getStatements(pc)[0]
            println("Executing $pc $stmt")
            stmt.execute(machine)
            pc = program.nextLineNumber(pc)
        }
        println("Stopped $program")
    }

}