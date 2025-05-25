package com.example.bluehits.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.drag
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
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import com.example.interpreter.models.Id
import kotlin.math.sqrt

@Composable
fun CreateCanvas(
    blocks: List<BlueBlock>,
    textMeasurer: TextMeasurer,
    connectionManager: UIConnectionManager,
    onDrag: (dragAmount: Offset) -> Unit,
    onBlockDrag: (block: BlueBlock, dragAmount: Offset, isDragging: Boolean) -> Unit,
    onBlockClick: (blockId: Id) -> Unit,
    onConnectionError: (String) -> Unit
) {
    var canvasOffset by remember { mutableStateOf(Offset.Zero) }
    var scale by remember { mutableStateOf(1f) }
    var selectedBlock by remember { mutableStateOf<BlueBlock?>(null) }
    val lineCreator = remember { LineCreator() }
    val density = LocalDensity.current

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF212121))
            .pointerInput(blocks) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val startPosition = down.position
                    val adjusted = (startPosition - canvasOffset) / scale

                    selectedBlock = blocks.firstOrNull { block ->
                        adjusted.x in block.x..(block.x + block.width) &&
                                adjusted.y in block.y..(block.y + block.height)
                    }

                    if (selectedBlock != null) {
                        onBlockDrag(selectedBlock!!, Offset.Zero, true)
                    }

                    var pointerCount = 1
                    var pastTouchSlop = false
                    var previousCentroid = down.position
                    var previousDistance = 0f

                    while (true) {
                        val event = awaitPointerEvent(pass = PointerEventPass.Main)
                        val pointers = event.changes.filter { it.pressed }
                        if (pointers.isEmpty()) break

                        pointerCount = pointers.size

                        if (pointerCount == 1) {
                            val change = pointers[0]
                            val dragAmount = change.positionChange()

                            selectedBlock?.let {
                                onBlockDrag(it, dragAmount / scale, true)
                            } ?: run {
                                canvasOffset += dragAmount
                                onDrag(dragAmount)
                            }
                            change.consume()
                        } else if (pointerCount == 2) {
                            val pos1 = pointers[0].position
                            val pos2 = pointers[1].position
                            val newCentroid = (pos1 + pos2) / 2f
                            val newDistance = (pos1 - pos2).getDistance()

                            if (!pastTouchSlop) {
                                pastTouchSlop = true
                                previousCentroid = newCentroid
                                previousDistance = newDistance
                            }

                            val zoom = newDistance / previousDistance
                            val newScale = (scale * zoom).coerceIn(0.5f, 1f)

                            if (newScale != scale) {
                                val adjustedCentroid = (newCentroid - canvasOffset) / scale
                                scale = newScale
                                canvasOffset = newCentroid - (adjustedCentroid * scale)
                            }

                            val pan = newCentroid - previousCentroid
                            canvasOffset += pan
                            onDrag(pan)

                            previousCentroid = newCentroid
                            previousDistance = newDistance

                            pointers.forEach { it.consume() }
                        }
                    }

                    selectedBlock?.let { onBlockDrag(it, Offset.Zero, false) }
                    selectedBlock = null
                }
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

private fun Offset.getDistance(): Float = sqrt(x * x + y * y)