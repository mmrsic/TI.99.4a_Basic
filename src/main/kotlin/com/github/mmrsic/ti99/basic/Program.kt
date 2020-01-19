package com.github.mmrsic.ti99.basic

import java.util.*

class ProgramLine(val lineNumber: Int, val statements: List<Statement>)


sealed class Program(private val lines: TreeMap<Int, ProgramLine> = TreeMap()) {

    fun store(line: ProgramLine) {
        if (line.lineNumber < 1 || line.lineNumber > 32767) {
            throw BadLineNumber()
        }
        for (statement in line.statements) {
            if (statement is LetNumberStatement && statement.varName.length > 15) {
                throw BadName()
            }
        }
        lines[line.lineNumber] = line
    }

    fun getStatements(lineNumber: Int): List<Statement> {
        val line = lines[lineNumber] ?: throw Exception("No such line: $lineNumber")
        return line.statements
    }

    /** Check whether this program statements for a given line number. */
    fun hasLineNumber(lineNumber: Int): Boolean = lines.containsKey(lineNumber)

    fun firstLineNumber(): Int = lines.firstKey()
    fun nextLineNumber(lineNumber: Int): Int? = lines.higherKey(lineNumber)
    fun lastLineNumber(): Int = lines.lastKey()

    /** RESEQUENCE the line numbers of this program for a given initial line number and a given increment. */
    fun resequence(initialLine: Int, increment: Int) {
        val oldLines = lines.toMap()
        lines.clear()
        val lineMapping = mutableMapOf<Int, Int>()
        oldLines.values.forEachIndexed { entryIdx, oldProgramLine ->
            val newLineNum = initialLine + increment * entryIdx
            lines[newLineNum] = ProgramLine(newLineNum, oldProgramLine.statements)
            lineMapping[oldProgramLine.lineNumber] = newLineNum
        }
        for (programLine in lines.values) {
            for (stmt in programLine.statements) {
                stmt.adjustLineNumbers(lineMapping)
            }
        }
        println("Resequenced: $lineMapping")
    }

    class LineResult {

        object End
        open class Execute(val lineNumber: Int)
        class Gosub(lineNumber: Int) : Execute(lineNumber)
    }

    private fun Statement.adjustLineNumbers(linNumbersMapping: Map<Int, Int>) {
        if (this is LineNumberDependentStatement) {
            this.changeLineNumbers(linNumbersMapping)
        }
    }

}

class TiBasicProgram : Program()
