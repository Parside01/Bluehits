package com.example.interpreter.models

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.BufferedWriter
import java.io.StringWriter

class ProgramTest {
    @Test
    fun testSimpleAddProgram() {
        val aIntBlock = BlockManager.createIntBlock(value =10)
        val bIntBlock = BlockManager.createIntBlock(value =10)
        val addBlock = BlockManager.createAddBlock()

        ConnectionManager.connect(aIntBlock.outputs.single(), addBlock.inputs.first())
        ConnectionManager.connect(bIntBlock.outputs.single(), addBlock.inputs.last())

        Program.run()

        assertEquals((aIntBlock.inputs.first().getValue() as Int) + (bIntBlock.inputs.first().getValue() as Int), addBlock.outputs.first().getValue())
    }

    @Test
    fun testProgramWithIfElse() {
        val stringWriter = StringWriter()
        val bufferedWriter = BufferedWriter(stringWriter)

        val aPrintBlock = BlockManager.createPrintBlock(bufferedWriter)
        val bPrintBlock = BlockManager.createPrintBlock(bufferedWriter)

        aPrintBlock.inputs.single().setValue("if")
        bPrintBlock.inputs.single().setValue("else")

        val boolBlock = BlockManager.createBoolBlock(value =true)
        val ifElseBlock = BlockManager.createIfElseBlock()

        ConnectionManager.connect(boolBlock.outputs.first(), ifElseBlock.inputs.first())
        ConnectionManager.connect(ifElseBlock.outputs.first(), aPrintBlock.blockPin)
        ConnectionManager.connect(ifElseBlock.outputs.last(), bPrintBlock.blockPin)

        Program.run()

        val output = stringWriter.toString()
        assertTrue(output.contains("if") && !output.contains("else"), "Output should contain 'if'")
    }
}