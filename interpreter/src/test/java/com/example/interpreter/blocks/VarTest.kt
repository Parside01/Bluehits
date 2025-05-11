package com.example.interpreter.blocks

import com.example.interpreter.models.BlockManager
import interpreter.models.Connection
import org.junit.jupiter.api.Test

class VarTest {
    @Test
    fun testDefaultVarIntValue() {
        val intBlock = BlockManager.createIntBlock(10)
        intBlock.execute()

        val output = intBlock.outputs.single().getValue()
        assert(output == 10)
    }

    @Test
    fun testDefaultVarBoolValue() {
        var boolBlock = BlockManager.createBoolBlock(true)
        boolBlock.execute()
        val output = boolBlock.outputs.single().getValue()
        assert(output == true)
    }

    @Test
    fun testRelatedIntVars() {
        val testValue = 10
        val aIntBlock = BlockManager.createIntBlock(testValue)
        val bIntBlock = BlockManager.createIntBlock()

        val connection = Connection(aIntBlock.outputs.single(), bIntBlock.inputs.single())

        aIntBlock.execute()
        connection.execute()
        bIntBlock.execute()

        assert(bIntBlock.outputs.size == 1) {
            "Expected bIntBlock to have 1 output, but found ${bIntBlock.outputs.size}."
        }
        assert(bIntBlock.outputs.single().getValue() == testValue) {
            "Expected output value to be $testValue, but found ${bIntBlock.outputs.single().getValue()}."
        }
    }
}