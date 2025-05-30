package com.example.bluehits.ui

import androidx.compose.ui.graphics.Color
import com.example.interpreter.blocks.ArrayBlock
import com.example.interpreter.blocks.BoolBlock
import com.example.interpreter.blocks.FloatBlock
import com.example.interpreter.blocks.FunctionCallBlock
import com.example.interpreter.blocks.FunctionDefinitionBlock
import com.example.interpreter.blocks.FunctionReturnBlock
import com.example.interpreter.blocks.IntBlock
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

    fun wrapLogicBlock(logicBlock: Block, centerX: Float = 0f, centerY: Float = 0f): BlueBlock {
        val title = when (logicBlock) {
            is FunctionDefinitionBlock -> "def ${logicBlock.getFunctionName()}"
            is FunctionCallBlock -> "call ${logicBlock.getFunctionName()}"
            is FunctionReturnBlock -> "return ${logicBlock.getFunctionName()}"
            is IntBlock -> "Int ${logicBlock.name}"
            is FloatBlock -> "Float ${logicBlock.name}"
            is BoolBlock -> "Bool ${logicBlock.name}"
            else -> logicBlock.name
        }

        return BlueBlock(
            id = logicBlock.id,
            initialX = centerX,
            initialY = centerY,
            color = BlockBodyColor,
            title = title,
            inputPins = logicBlock.inputs,
            outputPins = logicBlock.outputs,
            inBlockPin =  when (logicBlock) {
                is FunctionDefinitionBlock -> null
                else -> logicBlock.blockPin
            },
            outBlockPin = when (logicBlock) {
                is FunctionReturnBlock -> null
                else -> logicBlock.outBlockPin
            },
            functionName = when (logicBlock) {
                is FunctionDefinitionBlock -> logicBlock.getFunctionName()
                is FunctionCallBlock -> logicBlock.getFunctionName()
                is FunctionReturnBlock -> logicBlock.getFunctionName()
                else -> logicBlock.name
            }
        )
    }
}