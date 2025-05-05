package interpreter.blocks

import interpreter.models.BlockManager
import interpreter.models.Connection
import org.junit.jupiter.api.Test

class IfElseTest {
    @Test
    fun testIfTest() {
        val boolBlock = BlockManager.createBoolBlock(false)
        val ifElseBlock = BlockManager.createIfElseBlock()

        Connection(boolBlock.outputs.single(), ifElseBlock.inputs.single())

        boolBlock.execute()
        ifElseBlock.execute()

        val output = ifElseBlock.outputs[0].getValue()
        println(output.toString())
        assert(output == 0)
    }
}