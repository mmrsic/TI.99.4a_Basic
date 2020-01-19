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

/**
 * When the list command is entered, the program lines specified by the line-list are displayed. If a
 * device-name is entered, then the specified program lines are printed on the specified device. Device-names
 * for possible future accessory devices will be given in their respective manuals. If no device-name is
 * entered, the specified lines are displayed on the screen.
 * If the LIST command is entered with no line-list, then the entire program is displayed. The program lines
 * are always listed in ascending order. Note that all unnecessary blank spaces that were present when you
 * entered the program line were deleted when the computer accepted the line. Notice that when you list the
 * lines, unnecessary blank spaces have been deleted.
 *
 * If the line-list is entered, it may consist of a single number, a single number preceded by a hyphen (for
 * example: -10), a single number followed by a hyphen (for example: 10-), or a hyphenated range of line
 * numbers. If the line-list is:
 * * A single number - only the program line for the line number specified is displayed
 * * A single number preceded by a hyphen - all program lines with line numbers less than or equal to the line
 * number specified are displayed
 * * A single number followed by a hyphen - all program lines with line numbers greater than or equal to the line
 * number specified are displayed
 * * A hyphenated range of line numbers - all program lines with line numbers not less than the first line number
 * in the range and not greater than the second line number are displayed
 */
class ListCommand(val start: Int?, val end: Int?) : Command {
    override val name: String = "LIST"

    /** Whether a hyphenated range of line numbers was specified. */
    var isRange = true
        private set

    constructor(line: Int?) : this(line, null) {
        isRange = false
    }

    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        if (machine.program == null) {
            throw CantDoThat()
        }
        when {
            isRange -> machine.listProgram(start, end)
            start != null -> machine.listProgram(start, start)
            else -> machine.listProgram()
        }
    }
}

data class RunCommand(val line: Int?) : Command {
    override val name: String = "RUN"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        if (line != null && machine.program != null && !machine.program!!.hasLineNumber(line)) {
            throw BadLineNumber()
        }
        machine.resetCharacters()
        machine.resetVariables()
        val runResult = TiBasicProgramInterpreter(machine).interpretAll(line)
        if (runResult == null) {
            machine.screen.print("")
            machine.screen.print("** DONE **")
        }
    }
}

class ByeCommand : Command {
    override val name: String = "BYE"
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        machine.leave()
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

class NumberCommand(val initialLine: Int = 100, val increment: Int = 10) : TiBasicExecutable {
    override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
        println("Not yet implemented: NUM[BER] $initialLine,$increment")
    }
}