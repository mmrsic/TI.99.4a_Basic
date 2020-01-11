package com.github.mmrsic.ti99.basic

import com.github.mmrsic.ti99.hw.TiBasicModule

interface Command : TiBasicExecutable {
    /** Name of this command.*/
    val name: String
}


/**
 * The NEW command erases the program that is currently stored in memory. Entering the NEW command cancels
 * the effect of the [BreakCommand] and the [TraceCommand]. The NEW command also closes any open files
 * (see [OpenStatement]) and releases all space that had been allocated for special characters.
 * In addition, the NEW command erases all variable values and the table in which variable names are stored.
 * After the NEW command is performed, the screen is cleared and the message "TI BASIC READY" is displayed
 * on the screen. The prompt and flashing cursor indicate that you may enter another command or a program line.
 */
class NewCommand() : Command {
    override val name: String = "NEW"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        machine.eraseProgram()
        machine.cancelBreak()
        machine.cancelTrace()
        machine.closeOpenFiles()
        machine.resetCharacters()
        machine.resetVariables()
        machine.initCommandScreen()
    }
}

class ListCommand(val start: Int?, val end: Int?) : Command {
    override val name: String = "LIST"

    var isRange = true
        private set

    constructor(line: Int?) : this(line, null) {
        isRange = false
    }

    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        if (machine.program == null) {
            throw CantDoThat()
        }
        machine.listProgram()
    }
}

data class RunCommand(val line: Int?) : Command {
    override val name: String = "RUN"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        machine.resetVariables()
        val runResult = TiBasicProgramInterpreter(machine).interpretAll()
        if (runResult == null) {
            machine.screen.print("")
            machine.screen.print("** DONE **")
        }
    }
}

class ByeCommand : Command {
    override val name: String = "BYE"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class StoreProgramLineCommand(private val programLine: ProgramLine) : Command {
    override val name = "-- IMPLICIT STORE --"

    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        if (programLine.lineNumber < 1 || programLine.lineNumber > 32767) {
            throw BadLineNumber()
        }
        machine.closeOpenFiles()
        machine.resetVariables()
        machine.store(programLine)
    }
}