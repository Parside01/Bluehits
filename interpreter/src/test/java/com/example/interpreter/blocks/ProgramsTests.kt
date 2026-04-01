package com.example.interpreter.blocks

import com.example.interpreter.models.BlockManager
import com.example.interpreter.models.ConnectionManager
import com.example.interpreter.models.Program
import org.junit.jupiter.api.Test

class ProgramsTest {
//    @Test
//    fun testLoop() {
//        val mainBlock = Program.getMainBlock()
//        val forBlock = BlockManager.createForBlock()
//        val printBlock = BlockManager.createPrintBlock()
//
//        ConnectionManager.connect(mainBlock.outputs.first(), forBlock.blockPin)
//        ConnectionManager.connect(forBlock.outputs.first(), printBlock.blockPin)
//
//        forBlock.inputs[0].setValue(0)
//        forBlock.inputs[1].setValue(100)
//        forBlock.inputs[2].setValue(1)
//
//        Program.run()
//    }

    @Test
    fun testNestedLoops() {
        val mainBlock = Program.getMainBlock()
        val forBlockFirst = BlockManager.createForBlock()
        val forBlockSecond = BlockManager.createForBlock()
        val printBlock = BlockManager.createPrintBlock()

        ConnectionManager.connect(mainBlock.outputs.first(), forBlockFirst.blockPin)
        ConnectionManager.connect(forBlockFirst.outputs.first(), forBlockSecond.blockPin)
        ConnectionManager.connect(forBlockSecond.outputs.first(), printBlock.blockPin)

        forBlockFirst.inputs[0].setValue(0)
        forBlockFirst.inputs[1].setValue(10)
        forBlockFirst.inputs[2].setValue(1)

        forBlockSecond.inputs[0].setValue(0)
        forBlockSecond.inputs[1].setValue(10)
        forBlockSecond.inputs[2].setValue(1)

        Program.run()
    }
}