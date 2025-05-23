package com.example.interpreter.blocks

import com.example.interpreter.models.BlockManager
import com.example.interpreter.models.Connection
import org.junit.jupiter.api.Test

class IfElseTest {
    @Test
    fun testIfTest() {
        val boolBlock = BlockManager.createBoolBlock(value = false)
        val ifElseBlock = BlockManager.createIfElseBlock()

        val connection = Connection(boolBlock.outputs.single(), ifElseBlock.inputs.single())

        boolBlock.execute()
        connection.execute()
        ifElseBlock.execute()


        val output = ifElseBlock.outputs[0].getValue()
        assert(output == ifElseBlock.outputs[1].getValue())
    }
}