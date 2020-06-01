package com.github.mmrsic.ti99.basic

import com.github.mmrsic.ti99.basic.expr.StringExpr
import com.github.mmrsic.ti99.hw.TiBasicModule

/**
 * A command is a [TiBasicExecutable] which may be interpreted by a [TiBasicCommandLineInterpreter].
 */
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
class NewCommand : Command {

   override val name: String = "NEW"
   override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
      machine.eraseProgram()
      machine.cancelBreak()
      machine.traceProgramExecution = false
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
class ListCommand(val start: Int? = null, val end: Int? = null, val isRange: Boolean = false,
                  private val device: StringExpr? = null) : Command {

   override val name: String = "LIST"

   override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
      if (machine.program == null) throw CantDoThat()
      if (device != null) throw InOutError("00")
      when {
         isRange -> machine.listProgram(start, end)
         start != null -> machine.listProgram(start, start)
         else -> machine.listProgram()
      }
   }

   override fun requiresEmptyLineAfterExecution() = false
}

/**
 * Existing program lines may be changed by entering Edit Mode.
 */
class EditCommand(val lineNumber: Int) : Command {

   override val name = "EDIT"

   override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
      if (programLineNumber != null) throw  CantDoThat()
      val program = machine.program ?: throw CantDoThat()
      if (!program.hasLineNumber(lineNumber)) throw BadLineNumber()
      TODO("Not yet implemented: EDIT program line")
   }
}

class RunCommand(val line: Int?) : Command {
   override val name: String = "RUN"
   override fun execute(machine: TiBasicModule, programLineNumber: Int?) = machine.runProgram(line)
   override fun requiresEmptyLineAfterExecution() = false
}

/**
 * The BYE command ends TI Basic and returns the computer to the master title screen. All open files are closed, all
 * program lines are erased and the computer is reset.
 */
class ByeCommand : Command {

   override val name: String = "BYE"
   override fun execute(machine: TiBasicModule, programLineNumber: Int?) = machine.leave()
}

class StoreProgramLineCommand(private val programLine: ProgramLine) : Command {
   override val name = "-- IMPLICIT STORE --"
   override fun execute(machine: TiBasicModule, programLineNumber: Int?) = machine.store(programLine)
   override fun requiresEmptyLineAfterExecution() = false
}

class RemoveProgramLineCommand(private val lineNumber: Int) : Command {
   override val name = "-- IMPLICIT REMOVAL --"
   override fun execute(machine: TiBasicModule, programLineNumber: Int?) = machine.removeProgramLine(lineNumber)
   override fun requiresEmptyLineAfterExecution() = false
}

/** A dummy command executed on storing  */
class CantDoThatProgramLineCommand : TiBasicModule.ExecutedOnStore, Command {

   override fun onStore(lineNumber: Int, machine: TiBasicModule) = throw CantDoThat()
   override val name = "-- CAN'T DO THAT ERROR --"
   override fun execute(machine: TiBasicModule, programLineNumber: Int?) = throw CantDoThat()
}

class NumberCommand(val initialLine: Int = 100, val increment: Int = 10) : Command {
   override val name = "NUMBER"
   override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
      println("Not yet implemented: NUM[BER] $initialLine,$increment")
   }
}

/**
 * When the RESEQUENCE command is entered, all lines in the program are assigned new line numbers
 * according to the specified initial-line and increment.
 * @param initialLine a strictly positive value less than or equal to 32767
 * @param increment a strictly positive value
 */
class ResequenceCommand(val initialLine: Int = 100, val increment: Int = 10) : Command {

   override val name = "RESEQUENCE"
   override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
      machine.resequenceProgram(initialLine, increment)
   }

   override fun requiresEmptyLineAfterExecution() = false
}

/**
 * The BREAK [Command] requires a line-number-list. It causes the program to stop immediately before the lines in
 * the line-number-list are executed. After a breakpoint is taken because the line is listed in the line-number-
 * list, the breakpoint is removed and no more breakpoints occur at that line unless a new break command or [Statement]
 * is given.
 * BREAK is useful in finding out why a program is not running exactly as you expect it to. When the program has
 * stopped you can print values of variables to find out what is happening in the program. You may enter any command
 * or statement that can be used as a command. If you edit the program, however, you cannot resume with the
 * [ContinueCommand].
 * A way to remove breakpoints set with BREAK followed by line numbers is the [UnbreakCommand]. Also, if a breakpoint
 * is set at a program line and that line is deleted, the breakpoint is removed. Breakpoints are also removed when a
 * program is saved with the [SaveCommand]. See the [OnBreakStatement] for a way to handle breakpoints.
 * Whenever a breakpoint occurs, the standard character set is restored. Thus any standard characters that had been
 * redefined by the [CharSubprogram] are restored to the standard characters. A breakpoint also restores the standard
 * colors.
 */
class BreakCommand(private val lineNumberList: List<Int>) : Command {

   override val name = "BREAK"
   override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
      if (programLineNumber != null) {
         throw IllegalArgumentException("Break command may not be used in program: $programLineNumber")
      }
      machine.addBreakpoints(lineNumberList)
   }
}

class ContinueCommand : Command {
   override val name = "CONTINUE"
   override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
      if (programLineNumber != null) {
         throw IllegalArgumentException("Continue command may not be used in program: $programLineNumber")
      }
      machine.continueProgram()
   }

   override fun requiresEmptyLineAfterExecution() = false
}

/**
 * The UNBREAK [Command] removes all breakpoints. It can optionally set for only those in line-list. UNBREAK can be
 * used as a [Statement].
 */
class UnbreakCommand(private val lineList: List<Int> = listOf()) : Command, Statement {

   override val name: String = "UNBREAK"
   override fun execute(machine: TiBasicModule, programLineNumber: Int?) = machine.removeBreakpoints(lineList)

   override fun listText() = "$name $lineList"
}

/**
 * The TRACE command allows you to see the order in which the computer performs statements as it runs a program.
 * After the TRACE command is entered, the line number of each program line is displayed before the statement is
 * performed. The TRACE command may be placed as a statement in a program.
 */
class TraceCommand : Command, Statement {

   override val name = "TRACE"
   override fun listText() = name
   override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
      machine.traceProgramExecution = true
   }
}

/**
 * The UNTRACE command cancels the effect of the [TraceCommand]. The UNTRACE command may be placed as a statement
 * in a program.
 */
class UntraceCommand : Command, Statement {

   override val name = "UNTRACE"
   override fun listText() = name
   override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
      machine.traceProgramExecution = false
   }
}

/**
 * The SAVE command allows you to copy the current program in the computer's memory onto an accessory device. By using
 * [OldCommand], you can later put the program into memory for running or editing.
 */
class SaveCommand(private val deviceAndFile: StringExpr) : Command {

   override val name = "SAVE"

   override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
      TODO("not implemented")
   }
}

/**
 * The OLD command copies a previously saved program into the computer's memory. You can then run, list, or change the program.
 */
class OldCommand(private val deviceAndFile: StringExpr) : Command {

   override val name: String
      get() = TODO("Not yet implemented")

   override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
      TODO("not implemented")
   }
}

class DeleteCommand(private val deviceAndFile: StringExpr) : Command, Statement {
   override val name: String
      get() = TODO("Not yet implemented")

   override fun listText(): String {
      TODO("not implemented")
   }

   override fun execute(machine: TiBasicModule, programLineNumber: Int?) {
      TODO("not implemented")
   }
}
