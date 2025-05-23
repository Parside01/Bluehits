package com.example.bluehits.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.interpreter.models.Block
import com.example.interpreter.models.Program

object BlockAdapter {
    private val colorMapping = mapOf(
        "Int" to Color(0xFF4CAF50),
        "Bool" to Color(0xFF9C27B0),
        "Add" to Color(0xFF2196F3),
        "Print" to Color(0xFF488185),
        "Sub" to Color(0xFF404747),
        "IfElse" to Color(0xFFe3b202),
        "For" to Color (0xFFc93c20)
    )

    fun wrapLogicBlock(logicBlock: Block): BlueBlock {
        return BlueBlock(
            id = logicBlock.id,
            initialX = 0f,
            initialY = 0f,
            color = Color.Gray,
            title = logicBlock.name ?: "Block",
            inputPins = logicBlock.inputs,
            outputPins = logicBlock.outputs,
            blockPin = logicBlock.blockPin,
        ).apply {
            this.logicBlock = logicBlock
        }
    }
}