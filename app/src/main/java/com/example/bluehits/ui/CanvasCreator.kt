package com.example.bluehits.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
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
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import com.example.interpreter.models.Id
import kotlin.math.abs

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
    var scale by remember { mutableStateOf(1f) } // Начальный масштаб 1
    var selectedBlock by remember { mutableStateOf<BlueBlock?>(null) }
    val connectionManager = remember { UIConnectionManager() }
    val lineCreator = remember { LineCreator() }
    val density = LocalDensity.current

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF212121))
            .pointerInput(Unit) {
                forEachGesture {
                    awaitPointerEventScope {
                        var isDraggingBlock = false
                        var dragStartOffset = Offset.Zero
                        var lastPosition = Offset.Zero
                        var isZooming = false
                        val firstDown = awaitFirstDown()
                        dragStartOffset = firstDown.position
                        lastPosition = dragStartOffset
                        val adjustedStart = (dragStartOffset - canvasOffset) / scale

                        if (scale == 1f) {
                            selectedBlock = blocks.firstOrNull { block ->
                                adjustedStart.x in block.x..(block.x + block.width) &&
                                        adjustedStart.y in block.y..(block.y + block.height)
                            }
                            isDraggingBlock = selectedBlock != null
                            if (isDraggingBlock) {
                                selectedBlock?.let { block ->
                                    onBlockDrag(block, Offset.Zero, true)
                                }
                            }
                        }

                        while (true) {
                            val event = awaitPointerEvent()
                            val position = event.changes.firstOrNull()?.position ?: continue

                            if (event.changes.size == 1 && !isZooming) {
                                val dragAmount = position - lastPosition
                                if (isDraggingBlock && scale == 1f) {
                                    selectedBlock?.let { block ->
                                        onBlockDrag(block, dragAmount / scale, true)
                                    }
                                } else {
                                    canvasOffset += dragAmount
                                    onDrag(dragAmount)
                                }
                                lastPosition = position
                            }

                            else if (event.changes.size >= 2) {
                                isZooming = true
                                val zoom = event.calculateZoom()
                                val pan = event.calculatePan()
                                val newScale = (scale * zoom).coerceIn(0.5f, 1f)
                                if (abs(newScale - scale) > 0.01f) {
                                    scale = newScale
                                }
                                canvasOffset += pan
                                onDrag(pan)
                                lastPosition = position
                            }

                            if (event.type == PointerEventType.Release && event.changes.all { !it.pressed }) {
                                if (scale == 1f && !isDraggingBlock && !isZooming) {
                                    val adjustedOffset = (dragStartOffset - canvasOffset) / scale
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
                                break
                            }
                        }

                        selectedBlock?.let { block ->
                            onBlockDrag(block, Offset.Zero, false)
                        }
                        selectedBlock = null
                        isDraggingBlock = false
                        isZooming = false
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

                if (block == selectedBlock && scale == 1f) {
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