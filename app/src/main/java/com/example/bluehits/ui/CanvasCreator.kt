package com.example.bluehits.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.platform.LocalContext
import com.example.interpreter.models.BlockManager

@Composable
fun createCanvas(blocks: List<BlueBlock>,
                 textMeasurer: TextMeasurer,
                 onDrag: (dragAmount: Offset) -> Unit,
                 onBlockDrag: (block: BlueBlock,
                               dragAmount: Offset) -> Unit) {
    var canvasOffset by remember { mutableStateOf(Offset.Zero) }
    var selectedBlock by remember { mutableStateOf<BlueBlock?>(null) }
    val context = LocalContext.current
    val connectionManager = remember { UIConnectionManager() }
    val lineCreator = remember { LineCreator() }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF212121))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val adjusted = offset - canvasOffset
                        selectedBlock = blocks.firstOrNull { block ->
                            adjusted.x in block.x..(block.x + block.width) &&
                                    adjusted.y in block.y..(block.y + block.height)
                        }
                    },
                    onDrag = { _, dragAmount ->
                        selectedBlock?.let {
                            onBlockDrag(it, dragAmount)
                        } ?: run {
                            canvasOffset += dragAmount
                            onDrag(dragAmount)
                        }
                    },
                    onDragEnd = { selectedBlock = null }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures {  offset ->
                    val adjustedOffset = offset - canvasOffset
                    UIPinManager.findPinAt(adjustedOffset)?.let { pin ->
                        connectionManager.handlePinClick(pin)
                    }
                }
            }
    ) {
        translate(left = canvasOffset.x, top = canvasOffset.y) {
            blocks.forEach { block ->
                drawBlock(block, textMeasurer)

                if (block == selectedBlock) {
                    drawRect(
                        color = Color.White,
                        topLeft = Offset(block.x - 4, block.y - 4),
                        size = Size(block.width + 8, block.height + 8),
                        style = Stroke(width = 4f)
                    )
                }
            }
            connectionManager.connections.forEach { (pin1, pin2) ->
                lineCreator.run { drawBezierCurve(pin1, pin2) }
            }
        }
    }
}