//package com.example.interpreter.models
//
//import org.junit.jupiter.api.Test
//
//class ContextTest {
////    @Test
////    fun testTravelContextBlocks() {
////        val forBlock = BlockManager.createForBlock()
////        val firstIndex = BlockManager.createIntBlock(0)
////        val lastIndex = BlockManager.createIntBlock(10)
////        val step = BlockManager.createIntBlock(1)
////
////        ConnectionManager.connect(firstIndex.outputs.first(), forBlock.inputs[0])
////        ConnectionManager.connect(lastIndex.outputs.first(), forBlock.inputs[1])
////        ConnectionManager.connect(step.inputs.first(), forBlock.inputs[2])
////
////        val printBlock = BlockManager.createPrintBlock()
////        val intBlock = BlockManager.createIntBlock(101011)
////
////        ConnectionManager.connect(intBlock.outputs.first(), printBlock.inputs.first())
////        ConnectionManager.connect(forBlock.outputs.first(), intBlock.blockPin)
////
////        val ctx = Context(forBlock as ScopeBlock)
////        println(ctx.blocksList())
////    }
//
//    @Test
//    fun mainTest() {
//        val mainBlock = Program.getMainBlock()
//        val addBlock = BlockManager.createAddBlock()
//        val intABlock = BlockManager.createIntBlock(varName = "a", 10)
//        val intBBlock = BlockManager.createIntBlock(varName = "b", 10)
//        val printBlock = BlockManager.createPrintBlock()
//
//
//        ConnectionManager.connect(mainBlock.outputs.first(), addBlock.blockPin)
//        ConnectionManager.connect(intABlock.outputs.first(), addBlock.inputs.first())
//        ConnectionManager.connect(intBBlock.outputs.first(), addBlock.inputs.last())
//        ConnectionManager.connect(addBlock.outputs.first(), printBlock.inputs.first())
//
//        Program.run()
//    }
//}