package com.example.interpreter.blocks

import com.example.interpreter.models.BlockManager
import com.example.interpreter.models.Connection

import org.junit.jupiter.api.Test

class testBinaryOperation {
    @Test
    fun testAddBlock() {
        val aIntBlock = BlockManager.createIntBlock(value=10)
        val bIntBlock = BlockManager.createIntBlock(value=10)

        val addBlock = BlockManager.createAddBlock()

        aIntBlock.execute()
        bIntBlock.execute()

        Connection(aIntBlock.outputs.single(), addBlock.inputs[0]).execute()
        Connection(bIntBlock.outputs.single(), addBlock.inputs[1]).execute()

        addBlock.execute()

        val output = addBlock.outputs.single().getValue()

        assert(output == 20)
    }

    @Test
    fun testSubBlock() {
        val aIntBlock = BlockManager.createIntBlock(value=10)
        val bIntBlock = BlockManager.createIntBlock(value=10)

        val subBlock = BlockManager.createSubBlock()

        aIntBlock.execute()
        bIntBlock.execute()

        Connection(aIntBlock.outputs.single(), subBlock.inputs[0]).execute()
        Connection(bIntBlock.outputs.single(), subBlock.inputs[1]).execute()

        subBlock.execute()

        val output = subBlock.outputs.single().getValue()

        assert(output == 0)
    }
}