package com.example.bluehits

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput


class Rectangle(
    initialX: Float,
    initialY: Float,
    val color: Color,
    val size: Float
) {
    var x by mutableStateOf(initialX)
    var y by mutableStateOf(initialY)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var canvasOffsetX by remember { mutableStateOf(0f) }
            var canvasOffsetY by remember { mutableStateOf(0f) }
            var selectedRect by remember { mutableStateOf<Rectangle?>(null) }
            var isDraggingCanvas by remember { mutableStateOf(false) }

            val rectangles = remember {
                listOf(
                    Rectangle(100f, 100f, Color.Black, 100f),
                    Rectangle(300f, 300f, Color.Red, 100f),
                    Rectangle(500f, 500f, Color.Blue, 100f)
                )
            }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val adjustedX = offset.x - canvasOffsetX
                                val adjustedY = offset.y - canvasOffsetY

                                selectedRect = rectangles.firstOrNull { rect ->
                                    adjustedX in rect.x..(rect.x + rect.size) &&
                                            adjustedY in rect.y..(rect.y + rect.size)
                                }
                                isDraggingCanvas = (selectedRect == null)
                            },
                            onDrag = { change, dragAmount ->
                                if (isDraggingCanvas) {
                                    canvasOffsetX += dragAmount.x
                                    canvasOffsetY += dragAmount.y
                                } else {
                                    selectedRect?.let {
                                        it.x += dragAmount.x
                                        it.y += dragAmount.y
                                    }
                                }
                            },
                            onDragEnd = {
                                selectedRect = null
                                isDraggingCanvas = false
                            }
                        )
                    }
            ) {
                translate(left = canvasOffsetX, top = canvasOffsetY) {
                    rectangles.forEach { rect ->
                        if (rect == selectedRect) {
                            drawRect(
                                color = Color.Black.copy(alpha = 0.3f),
                                topLeft = Offset(rect.x + 8, rect.y + 8),
                                size = Size(rect.size, rect.size)
                            )
                        }

                        drawRect(
                            color = rect.color,
                            topLeft = Offset(rect.x, rect.y),
                            size = Size(rect.size, rect.size)
                        )

                        if (rect == selectedRect) {
                            drawRect(
                                color = Color.White,
                                topLeft = Offset(rect.x - 4, rect.y - 4),
                                size = Size(rect.size + 8, rect.size + 8),
                                style = Stroke(width = 4f)
                            )
                        }
                    }
                }
            }
        }
    }
}