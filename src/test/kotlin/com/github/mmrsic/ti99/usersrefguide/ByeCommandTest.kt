package com.github.mmrsic.ti99.usersrefguide

import com.github.mmrsic.ti99.basic.TiBasicCommandLineInterpreter
import com.github.mmrsic.ti99.hw.TiBasicModule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * See User Reference Guide in section Bye on page II-24.
 */
class ByeCommandTest {

    @Test
    fun testExample() {
        val machine = TiBasicModule()
        val interpreter = TiBasicCommandLineInterpreter(machine)
        interpreter.interpretAll(
            """
            100 LET X$="HELLO, GENIUS!"
            110 PRINT X$
            120 END
            RUN
            BYE
            """.trimIndent(), machine
        )

        assertNull(machine.program, "Program must be erased after BYE command")
        assertEquals(mapOf(), machine.getAllNumericVariableValues())
        assertEquals(mapOf(), machine.getAllStringVariableValues())
    }
}