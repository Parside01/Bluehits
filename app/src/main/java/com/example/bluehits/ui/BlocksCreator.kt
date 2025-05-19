package com.example.bluehits.ui

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.interpreter.models.BlockManager
import com.example.interpreter.models.Program

class BlocksManager {
    private val _uiBlocks = mutableStateListOf<BlueBlock>()
    val uiBlocks: List<BlueBlock> get() = _uiBlocks

    fun addNewBlock(type: String) {
        val logicBlock = when(type) {
            "Int" -> BlockManager.createIntBlock()
            "Add" -> BlockManager.createAddBlock()
            "Bool" -> BlockManager.createBoolBlock()
            "Print" -> BlockManager.createPrintBlock()
            "Sub" -> BlockManager.createSubBlock()
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
            color = Color.Cyan,
            width = 400f,
            height = 240f,
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
}