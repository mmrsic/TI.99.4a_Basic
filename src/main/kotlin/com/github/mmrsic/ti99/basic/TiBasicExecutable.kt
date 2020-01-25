package com.github.mmrsic.ti99.basic

import com.github.mmrsic.ti99.hw.TiBasicModule

/**
 * Any object that may be executed by the [TiBasicInterpreter].
 */
interface TiBasicExecutable {
    /** Execute this command for a given [TiBasicModule] machine and a given optional program line number.*/
    fun execute(machine: TiBasicModule, programLineNumber: Int? = null)

    /** Whether this command requires an empty print line after execution. */
    fun requiresEmptyLineAfterExecution(): Boolean = true
}