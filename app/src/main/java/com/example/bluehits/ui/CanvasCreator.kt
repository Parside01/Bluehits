package com.example.bluehits.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput

import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import com.example.interpreter.models.Id

@Composable
fun CreateCanvas(
    blocks: List<BlueBlock>,
    textMeasurer: TextMeasurer,
    onDrag: (dragAmount: Offset) -> Unit,
    onBlockDrag: (block: BlueBlock, dragAmount: Offset, isDragging: Boolean) -> Unit,
    onBlockClick: (blockId: Id) -> Unit,
    onConnectionError: (String) -> Unit
) {
    var canvasOffset by remember { mutableStateOf(Offset.Zero) }
    var scale by remember { mutableStateOf(1f) }
    var selectedBlock by remember { mutableStateOf<BlueBlock?>(null) }
    val connectionManager = remember { UIConnectionManager() }
    val lineCreator = remember { LineCreator() }
    val density = LocalDensity.current

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF212121))
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.5f, 3f)
                    canvasOffset += pan
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val adjusted = (offset - canvasOffset) / scale
                        selectedBlock = blocks.firstOrNull { block ->
                            adjusted.x in block.x..(block.x + block.width) &&
                                    adjusted.y in block.y..(block.y + block.height)
                        }
                        selectedBlock?.let { block ->
                            onBlockDrag(block, Offset.Zero, true)
                        }
                    },
                    onDrag = { _, dragAmount ->
                        selectedBlock?.let {
                            onBlockDrag(it, dragAmount / scale, true)
                        } ?: run {
                            canvasOffset += dragAmount
                            onDrag(dragAmount)
                        }
                    },
                    onDragEnd = {
                        selectedBlock?.let { block ->
                            onBlockDrag(block, Offset.Zero, false)
                        }
                        selectedBlock = null
                    }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val adjustedOffset = (offset - canvasOffset) / scale
                    UIPinManager.findPinAt(adjustedOffset)?.let { pin ->
                        connectionManager.handlePinClick(pin) { errorMessage ->
                            onConnectionError(errorMessage)
                        }
                    } ?: run {
                        blocks.firstOrNull { block ->
                            adjustedOffset.x in block.x..(block.x + block.width) &&
                                    adjustedOffset.y in block.y..(block.y + block.height)
                        }?.let { clickedBlock ->
                            onBlockClick(clickedBlock.id)
                        }
                    }
                }
            }
    ) {
        withTransform({
            translate(canvasOffset.x, canvasOffset.y)
            scale(scaleX = scale, scaleY = scale)
        }) {
            blocks.forEach { block ->
                drawBlock(block, textMeasurer, density)

                if (block == selectedBlock) {
                    drawRect(
                        color = Color.White,
                        topLeft = Offset(block.x - 4, block.y - 4),
                        size = Size(block.width + 8, block.height + 8),
                        style = Stroke(width = 4f / scale)
                    )
                }
            }
            connectionManager.connections.forEach { (pin1, pin2) ->
                lineCreator.run { drawBezierCurve(pin1, pin2) }
            }
        }
    }
}

