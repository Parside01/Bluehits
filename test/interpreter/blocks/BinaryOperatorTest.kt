package interpreter.blocks

import interpreter.models.BlockManager
import interpreter.models.Connection
import org.junit.jupiter.api.Test

class BinaryOperatorTest {
    @Test
    fun testAddBlock() {
        val aIntBlock = BlockManager.createIntBlock(10)
        val bIntBlock = BlockManager.createIntBlock(10)

        val addBlock = BlockManager.createAddBlock()

        Connection(aIntBlock.outputs.single(), addBlock.inputs[0])
        Connection(bIntBlock.outputs.single(), addBlock.inputs[1])

        aIntBlock.execute()
        bIntBlock.execute()

        addBlock.execute()

        val output = addBlock.outputs.single().getValue()

        assert(output == 20)
    }

    @Test
    fun testSubBlock() {
        val aIntBlock = BlockManager.createIntBlock(10)
        val bIntBlock = BlockManager.createIntBlock(10)

        val subBlock = BlockManager.createSubBlock()
        Connection(aIntBlock.outputs.single(), subBlock.inputs[0])
        Connection(bIntBlock.outputs.single(), subBlock.inputs[1])

        aIntBlock.execute()
        bIntBlock.execute()

        subBlock.execute()

        val output = subBlock.outputs.single().getValue()

        assert(output == 0)
    }
}