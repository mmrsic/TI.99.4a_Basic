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

    fun hasLineNumber(lineNumber: Int): Boolean = lines.containsKey(lineNumber)
    fun firstLineNumber(): Int = lines.firstKey()
    fun nextLineNumber(lineNumber: Int): Int? = lines.higherKey(lineNumber)
    fun previousLineNumber(lineNumber: Int): Int? = lines.lowerKey(lineNumber)
    fun lastLineNumber(): Int = lines.lastKey()

    class LineResult {
        object End
        open class Execute(val lineNumber: Int)
        class Gosub(lineNumber: Int) : Execute(lineNumber)
    }
}

class TiBasicProgram : Program()
