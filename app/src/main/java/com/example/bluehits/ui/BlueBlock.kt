package com.example.bluehits.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.bluehits.ui.pinCreator.drawPin
import interpreter.models.Pin
import interpreter.models.Block
import interpreter.models.Id

class BlockLayout(
    val totalWidth: Float,
    val totalHeight: Float,
    val pinWidth: Float = totalWidth * 0.15f,
    val titleHeight: Float = 30f
) {
    val leftPinArea = Rect(
        left = 0f,
        top = 0f,
        right = pinWidth,
        bottom = totalHeight
    )

    val rightPinArea = Rect(
        left = totalWidth - pinWidth,
        top = 0f,
        right = totalWidth,
        bottom = totalHeight
    )

    val centerArea = Rect(
        left = pinWidth,
        top = 0f,
        right = totalWidth - pinWidth,
        bottom = totalHeight
    )

    val titleArea = Rect(
        left = centerArea.left,
        top = 0f,
        right = centerArea.right,
        bottom = titleHeight
    )

    val contentArea = Rect(
        left = centerArea.left,
        top = titleHeight,
        right = centerArea.right,
        bottom = centerArea.bottom
    )
}

class BlueBlock(
    val id: Id,
    val initialX: Float,
    val initialY: Float,
    val color: Color,
    val width: Float,
    val height: Float,
    var title: String = "Block",
    val inputPins: List<Pin> = emptyList(),
    val outputPins: List<Pin> = emptyList()
) {
    val leftPins: List<Offset>
        get() = calculateVerticalPins(inputPins.size, layout.leftPinArea)

    val rightPins: List<Offset>
        get() = calculateVerticalPins(outputPins.size, layout.rightPinArea)

    var inputPinsUi: List<PinUi> = emptyList()
    var outputPinsUi: List<PinUi> = emptyList()

    var x by mutableStateOf(initialX)
    var y by mutableStateOf(initialY)
    var logicBlock: Block? = null

    val layout: BlockLayout
        get() = BlockLayout(width, height)

    private fun calculateVerticalPins(count: Int, area: Rect): List<Offset> {
        if (count == 0) return emptyList()

        val step = area.height / (count + 1)
        return List(count) { index ->
            Offset(
                x = area.center.x,
                y = area.top + step * (index + 1)
            )
        }
    }
}

fun DrawScope.drawBlock(
    block: BlueBlock,
    textMeasurer: androidx.compose.ui.text.TextMeasurer
) {
    drawRect(
        color = block.color,
        topLeft = Offset(block.x, block.y),
        size = Size(block.width, block.height),
        style = Fill
    )

    drawRect(
        color = block.color.copy(alpha = 0.8f),
        topLeft = Offset(block.x + block.layout.titleArea.left, block.y),
        size = Size(block.layout.titleArea.width, block.layout.titleArea.height),
        style = Fill
    )

    drawPins(block, block.leftPins, block.inputPins, "Input")
    drawPins(block, block.rightPins, block.outputPins,"Output")

    val textLayout = textMeasurer.measure(
        text = block.title,
        style = TextStyle(
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    )
    drawText(
        textLayoutResult = textLayout,
        topLeft = Offset(
            block.x + block.layout.titleArea.left + 10f,
            block.y + 10f
        )
    )

    drawRect(
        color = Color.Black,
        topLeft = Offset(block.x, block.y),
        size = Size(block.width, block.height),
        style = Stroke(width = 2f)
    )
}

fun DrawScope.drawPins(
    block: BlueBlock,
    pinsCoordinates: List<Offset>,
    logicPins: List<Pin>,
    type: String) {
    for (i in 0..pinsCoordinates.size - 1) {
        var ownOffset = Offset(pinsCoordinates[i].x, pinsCoordinates[i].y)
        var logicPin = logicPins[i]
        var newPin = pinCreator.createPin(ownOffset, block, type, logicPin)
        drawPin(newPin)
    }
}


