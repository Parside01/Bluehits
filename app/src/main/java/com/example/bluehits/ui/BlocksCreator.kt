package com.example.bluehits.ui

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

class BlocksManager {
    private val _blocks = mutableStateListOf<BlueBlock>()
    val blocks: List<BlueBlock> get() = _blocks

    fun addBlock(block: BlueBlock) {
        _blocks.add(block)
    }

    fun moveBlock(block: BlueBlock, delta: Offset) {
        _blocks.forEachIndexed { index, blueBlock ->
            if (blueBlock == block) {
                _blocks[index].x += delta.x
                _blocks[index].y += delta.y
            }
        }
    }
}

@Composable
fun createSampleBlocks(manager: BlocksManager) {
    LaunchedEffect(Unit) {
        manager.addBlock(
            BlueBlock(
                initialX = 200f,
                initialY = 200f,
                color = Color(0xFF2196F3),
                width = 300f,
                height = 150f,
                title = "Start",
                leftPinsCount = 1,
                rightPinsCount = 2
            )
        )
        manager.addBlock(
            BlueBlock(
                initialX = 100f,
                initialY = 500f,
                color = Color(0xFFE91E63),
                width = 280f,
                height = 180f,
                title = "End",
                leftPinsCount = 1,
                rightPinsCount = 1
            )
        )
    }
}