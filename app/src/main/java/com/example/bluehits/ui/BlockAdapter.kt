package com.example.bluehits.ui

import androidx.compose.ui.graphics.Color
import interpreter.models.Block

object BlockAdapter {
    private val colorMapping = mapOf(
        "Int" to Color(0xFF4CAF50),
        "Bool" to Color(0xFF9C27B0),
        "Add" to Color(0xFF2196F3),
        "Print" to Color(0xFF488185),
        "Sub" to Color(0xFF404747),
        "IfElse" to Color(0xFFe3b202)
    )

    fun wrapLogicBlock(logicBlock: Block): BlueBlock {
        return BlueBlock(
            id = logicBlock.id,
            initialX = 0f,
            initialY = 0f,
            color = colorMapping[logicBlock.name] ?: Color.Gray,
            width = 400f,
            height = 240f,
            title = logicBlock.name ?: "Block",
            inputPins = logicBlock.inputs,
            outputPins = logicBlock.outputs
        ).apply {
            this.logicBlock = logicBlock
        }
    }
}