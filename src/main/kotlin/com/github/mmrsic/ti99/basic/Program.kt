package com.github.mmrsic.ti99.basic

import com.github.mmrsic.ti99.hw.checkLineNumber
import java.util.*

class ProgramLine(val lineNumber: Int, val statements: List<Statement>)


sealed class Program(protected val lines: TreeMap<Int, ProgramLine> = TreeMap()) {

    fun store(line: ProgramLine) {
        val lineNumber = line.lineNumber
        checkLineNumber(lineNumber)
        for (statement in line.statements) {
            if (statement is LetNumberStatement && statement.varName.length > 15) throw BadName()
        }
        lines[lineNumber] = line
    }

    fun remove(lineNumber: Int): Boolean {
        return lines.remove(lineNumber) != null
    }

    /** All the statements found on a given line number. */
    fun getStatements(lineNumber: Int): List<Statement> {
        val line = lines[lineNumber] ?: throw Exception("No such line: $lineNumber")
        return line.statements
    }

    /** Execute a given lambda accepting the [ProgramLine] at a given line number. */
    fun withProgramLineNumberDo(lineNumber: Int, lambda: (ProgramLine) -> Unit) {
        val programLine = lines[lineNumber] ?: throw IllegalArgumentException("No such line: $lineNumber")
        lambda.invoke(programLine)
    }

    /** Check whether this program statements for a given line number. */
    fun hasLineNumber(lineNumber: Int): Boolean = lines.containsKey(lineNumber)

    /** The first, i.e. lowest, line number of this program. */
    fun firstLineNumber(): Int = lines.firstKey()

    /** The line number after a given line number as proposed by all line numbers of this program. */
    fun nextLineNumber(lineNumber: Int): Int? = lines.higherKey(lineNumber)

    /** The last, i.e. highest, line number of this program. */
    fun lastLineNumber(): Int = lines.lastKey()

    /** RESEQUENCE the line numbers of this program for a given initial line number and a given increment. */
    fun resequence(initialLine: Int, increment: Int) {
        val lineMapping = mutableMapOf<Int, Int>()
        lines.values.forEachIndexed { entryIdx, oldProgramLine ->
            val newLineNum = initialLine + increment * entryIdx
            checkLineNumber(newLineNum)
            lineMapping[oldProgramLine.lineNumber] = newLineNum
        }
        val oldLines = lines.toMap()
        lines.clear()
        for (programLine in oldLines.values) {
            val newLineNum: Int = lineMapping[programLine.lineNumber]!!
            lines[newLineNum] = ProgramLine(newLineNum, programLine.statements)
            for (stmt in programLine.statements) stmt.adjustLineNumbers(lineMapping)
        }
        println("Resequenced: $lineMapping")
    }

    // HELPERS //

    /** Adjust the line numbers of a statement to a given mapping from old to new line numbers. */
    private fun Statement.adjustLineNumbers(linNumbersMapping: Map<Int, Int>) {
        if (this is LineNumberDependentStatement) this.changeLineNumbers(linNumbersMapping)
    }
}

class TiBasicProgram : Program() {

    /** Find the minimum line satisfying a given predicate for its statements. */
    fun findLineWithStatement(minLine: Int, predicate: (Statement) -> Boolean): Int? {
        var candidate = lines[minLine]
        while (candidate != null) {
            if (candidate.statements.find(predicate) != null) return candidate.lineNumber
            candidate = lines[nextLineNumber(candidate.lineNumber)]
        }
        return null
    }
}
