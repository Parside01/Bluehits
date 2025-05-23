package com.example.bluehits.ui

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.interpreter.models.BlockManager
import com.example.interpreter.models.Program

class BlocksManager {
    private val _uiBlocks = mutableStateListOf<BlueBlock>()
    val uiBlocks: List<BlueBlock> get() = _uiBlocks

    public fun getPrintBlockValue(uiBlocks: List<BlueBlock>): Any? {
        uiBlocks.forEach { block ->
            if (block.title == "Print") {
                val logicBlock = BlockManager.getBlock(block.id)
                return logicBlock?.let { notNullBlock -> notNullBlock.inputs[0].getValue() }
            }
        }
        return null
    }


    fun addNewBlock(type: String) {
        val logicBlock = when (type) {
            "Index" -> BlockManager.createIndexBlock()
            "Append" -> BlockManager.createAppendBlock()
            "Array" -> BlockManager.createArrayBlock()
            "For" -> BlockManager.createForBlock()
            "Int" -> BlockManager.createIntBlock()
            "Add" -> BlockManager.createAddBlock(type=Int::class)
            "Bool" -> BlockManager.createBoolBlock()
            "Float" -> BlockManager.createFloatBlock()
            "Print" -> BlockManager.createPrintBlock()
            "Sub" -> BlockManager.createSubBlock(type=Int::class)
            "IfElse" -> BlockManager.createIfElseBlock()
            else -> throw IllegalArgumentException("Unsupported type")
        }
        _uiBlocks.add(BlockAdapter.wrapLogicBlock(logicBlock))
    }

    init {
        createMainBlockInUI()
    }

    fun createMainBlockInUI() {
        val mainLogicBlock = Program.getMainBlock()
        val mainBlueBlock = BlueBlock(
            id = mainLogicBlock.id,
            initialX = 0f,
            initialY = 0f,
            color = Color.Gray,
            title = mainLogicBlock.name ?: "Block",
            inputPins = mainLogicBlock.inputs,
            outputPins = mainLogicBlock.outputs,
            blockPin = mainLogicBlock.blockPin,
        )
        _uiBlocks.add(mainBlueBlock)
    }

    fun moveBlock(block: BlueBlock, delta: Offset) {
        val index = _uiBlocks.indexOf(block)
        if (index != -1) {
            _uiBlocks[index].x += delta.x
            _uiBlocks[index].y += delta.y
        }
    }

    fun removeBlock(block: BlueBlock) {
        _uiBlocks.remove(block)
    }
    fun clearAllBlocks() {
        _uiBlocks.clear()
    }
}