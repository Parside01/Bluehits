package com.example.interpreter.blocks

import com.example.interpreter.models.BlockManager
import com.example.interpreter.models.ConnectionManager
import com.example.interpreter.models.Program
import org.junit.jupiter.api.Test

class FunctionTest {

    @Test
    fun coreTest() {
        val mainBlock = Program.getMainBlock()
        val definitionBlock = BlockManager.createFunctionDefinitionBlock("Func")
        val callBlock = BlockManager.createFunctionCalledBlock("Func")
        val printBlock = BlockManager.createPrintBlock()

        ConnectionManager.connect(mainBlock.outputs.first(), callBlock.blockPin)
        ConnectionManager.connect(definitionBlock, printBlock.blockPin)
    }
}