package interpreter.models

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ProgramTest {
    @Test
    fun testSimpleAddProgram() {
        val aIntBlock = BlockManager.createIntBlock(10)
        val bIntBlock = BlockManager.createIntBlock(10)
        val addBlock = BlockManager.createAddBlock()

        ConnectionManager.connect(aIntBlock.outputs.single(), addBlock.inputs.first())
        ConnectionManager.connect(bIntBlock.outputs.single(), addBlock.inputs.last())

        Program.run()

        assertEquals((aIntBlock.inputs.first().getValue() as Int) + (bIntBlock.inputs.first().getValue() as Int), addBlock.outputs.first().getValue())
    }
}