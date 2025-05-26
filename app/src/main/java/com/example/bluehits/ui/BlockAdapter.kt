package com.example.bluehits.ui

import androidx.compose.ui.graphics.Color
import com.example.interpreter.blocks.FunctionCallBlock
import com.example.interpreter.blocks.FunctionDefinitionBlock
import com.example.interpreter.blocks.FunctionReturnBlock
import com.example.interpreter.models.Block

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
        val title = when (logicBlock) {
            is FunctionDefinitionBlock -> "def ${logicBlock.getFunctionName()}" ?: "definition"
            is FunctionCallBlock -> "call ${logicBlock.getFunctionName()}" ?: "call"
            is FunctionReturnBlock -> "return ${logicBlock.getFunctionName()}" ?: "return"
            else -> logicBlock.name ?: "Block"
        }

        return BlueBlock(
            id = logicBlock.id,
            initialX = 0f,
            initialY = 0f,
            color = BlockBodyColor,
            title = title,
            inputPins = logicBlock.inputs,
            outputPins = logicBlock.outputs,
            inBlockPin = logicBlock.blockPin,
            outBlockPin = logicBlock.outBlockPin,
            functionName = when (logicBlock) {
                is FunctionDefinitionBlock -> logicBlock.getFunctionName()
                is FunctionCallBlock -> logicBlock.getFunctionName()
                is FunctionReturnBlock -> logicBlock.getFunctionName()
                else -> null
            }
        )
    }
}