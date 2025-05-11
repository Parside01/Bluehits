package com.example.interpreter.blocks

import com.example.interpreter.models.BlockManager
import com.example.interpreter.models.ExecutionState
import org.junit.jupiter.api.Test

class ForTest {
    @Test
    fun testForBlockLessFirstIndex() {
        val forBlock = BlockManager.createForBlock()
        forBlock.inputs[0].setValue(0)
        forBlock.inputs[1].setValue(100)
        forBlock.inputs[2].setValue(20)
        assert(forBlock.execute() == ExecutionState.RUNNING)
    }
}