package com.github.mmrsic.ti99.basic

import com.github.mmrsic.ti99.hw.TiBasicModule

interface TiBasicExecutable {
    /** Execute this command for a given [TiBasicModule] machine.*/
    fun execute(machine: TiBasicModule)
}