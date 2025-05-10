package com.example.bluehits.ui

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import interpreter.models.BlockManager

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

    fun moveBlock(block: BlueBlock, delta: Offset) {
        val index = _uiBlocks.indexOf(block)
        if (index != -1) {
            _uiBlocks[index].x += delta.x
            _uiBlocks[index].y += delta.y
        }
    }
}