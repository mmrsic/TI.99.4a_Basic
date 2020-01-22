package com.github.mmrsic.ti99.basic

import java.util.*

class ProgramLine(val lineNumber: Int, val statements: List<Statement>)


sealed class Program(private val lines: TreeMap<Int, ProgramLine> = TreeMap()) {

    fun store(line: ProgramLine) {
        val lineNumber = line.lineNumber
        checkLineNumber(lineNumber)
        for (statement in line.statements) {
            if (statement is LetNumberStatement && statement.varName.length > 15) throw BadName()
        }
        lines[lineNumber] = line
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

    /**
     * Check whether a given line number is acceptable
     * @param lineNumber the line number to check
     * @throws BadLineNumber if the specified line number is not acceptable
     */
    private fun checkLineNumber(lineNumber: Int) {
        if (!isCorrectLineNumber(lineNumber)) throw BadLineNumber()
    }

    private fun isCorrectLineNumber(lineNumber: Int) = lineNumber in 1..32767

    private fun Statement.adjustLineNumbers(linNumbersMapping: Map<Int, Int>) {
        if (this is LineNumberDependentStatement) this.changeLineNumbers(linNumbersMapping)
    }

}

class TiBasicProgram : Program()
