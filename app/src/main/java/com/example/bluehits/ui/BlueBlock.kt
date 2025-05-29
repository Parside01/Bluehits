package com.example.bluehits.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bluehits.ui.pinCreator.drawBlockPin
import com.example.bluehits.ui.pinCreator.drawPin
import com.example.interpreter.blocks.FunctionDefinitionBlock
import com.example.interpreter.models.Pin
import com.example.interpreter.models.Block
import com.example.interpreter.models.Id
import kotlin.math.max

val BlockBodyColor = Color.Gray
val BlockTitleColor = Color(0xFF1E5F8B)
val BlockBorderColor = Color(0xFF1A1A1A)
val BlockShadowColor = Color(0x40000000)
val BlockTitleTextColor = Color(0xFFCCCCCC)
val PinTextColor = Color(0xFFFFFFFF)

val MIN_BLOCK_WIDTH_DP = 150.dp
val MIN_BLOCK_HEIGHT_DP = 80.dp
val PIN_TEXT_PADDING_DP = 2.dp
val PIN_RADIUS_DP = 6.dp

class BlockLayout(
    val totalWidth: Float,
    val totalHeight: Float,
    val pinWidth: Float = totalWidth * 0.15f,
    val titleHeight: Float = 40f
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
    initialX: Float,
    initialY: Float,
    val color: Color = BlockBodyColor,
    var title: String = "Block",
    val inputPins: List<Pin> = emptyList(),
    val outputPins: List<Pin> = emptyList(),
    val inBlockPin: Pin? = null,
    val outBlockPin: Pin? = null,
    val functionName: String? = null
) {
    var width by mutableStateOf(0f)
    var height by mutableStateOf(0f)

    var x by mutableStateOf(initialX)
    var y by mutableStateOf(initialY)
    var logicBlock: Block? = null

    val layout: BlockLayout
        get() = BlockLayout(width, height)

    val leftPins: List<Offset>
        get() = calculateVerticalPins(inputPins.size + 1, layout.leftPinArea)

    val rightPins: List<Offset>
        get() = calculateVerticalPins(outputPins.size + 1, layout.rightPinArea)


    fun updateSize(textMeasurer: TextMeasurer, density: Density) {
        val pinTextStyle = TextStyle(fontSize = 12.sp)
        val titleTextStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)

        val titleTextWidth = textMeasurer.measure(title, titleTextStyle).size.width.toFloat()

        var maxInputPinTextWidth = 0f
        inBlockPin?.let { pin ->
            maxInputPinTextWidth = max(
                maxInputPinTextWidth,
                textMeasurer.measure(pin.name, pinTextStyle).size.width.toFloat()
            )
        }
        inputPins.forEach { pin ->
            maxInputPinTextWidth =
                max(maxInputPinTextWidth, textMeasurer.measure(pin.name!!, pinTextStyle).size.width.toFloat())
        }

        var maxOutputPinTextWidth = 0f
        outputPins.forEach { pin ->
            maxOutputPinTextWidth =
                max(maxOutputPinTextWidth, textMeasurer.measure(pin.name!!, pinTextStyle).size.width.toFloat())
        }

        val pinTextPaddingPx: Float
        val pinRadiusPx: Float
        val minBlockWidthPx: Float
        val minBlockHeightPx: Float
        val bottomSpacingPx: Float
        val extraPinHeightPadding: Float

        with(density) {
            pinTextPaddingPx = PIN_TEXT_PADDING_DP.toPx()
            pinRadiusPx = PIN_RADIUS_DP.toPx()
            minBlockWidthPx = MIN_BLOCK_WIDTH_DP.toPx()
            minBlockHeightPx = MIN_BLOCK_HEIGHT_DP.toPx()
            bottomSpacingPx = 16.dp.toPx()
            extraPinHeightPadding = 10.dp.toPx()
        }


        val requiredContentWidth = max(
            titleTextWidth + (pinTextPaddingPx * 2),
            (maxInputPinTextWidth + maxOutputPinTextWidth) + (pinTextPaddingPx * 2)
        )

        val numInputPins = inputPins.size + 1
        val numOutputPins = outputPins.size

        val maxPins = max(numInputPins, numOutputPins)
        val requiredPinAreaHeight = (maxPins + 1) * (pinRadiusPx * 2 + pinTextPaddingPx * 2) + extraPinHeightPadding

        val requiredHeight = layout.titleHeight + requiredPinAreaHeight + bottomSpacingPx

        val calculatedWidth = (layout.pinWidth * 2) + requiredContentWidth + (pinTextPaddingPx * 2)
        this.width = max(minBlockWidthPx, calculatedWidth)
        this.height = max(minBlockHeightPx, requiredHeight)
    }


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
    textMeasurer: TextMeasurer,
    density: Density
) {
    val cornerRadius = 8.dp.toPx()
    val shadowOffset = 4.dp.toPx()
    val borderThickness = 2.dp.toPx()

    block.updateSize(textMeasurer, density)

    drawRoundRect(
        color = BlockShadowColor,
        topLeft = Offset(block.x + shadowOffset, block.y + shadowOffset),
        size = Size(block.width, block.height),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius),
        style = Fill
    )

    drawRoundRect(
        color = block.color,
        topLeft = Offset(block.x, block.y),
        size = Size(block.width, block.height),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius),
        style = Fill
    )

    val titlePath = Path().apply {
        val titleRect = Rect(
            left = block.x,
            top = block.y,
            right = block.x + block.width,
            bottom = block.y + block.layout.titleArea.height
        )

        moveTo(titleRect.left + cornerRadius, titleRect.top)

        lineTo(titleRect.right - cornerRadius, titleRect.top)
        arcTo(
            rect = Rect(
                left = titleRect.right - 2 * cornerRadius,
                top = titleRect.top,
                right = titleRect.right,
                bottom = titleRect.top + 2 * cornerRadius
            ),
            startAngleDegrees = 270f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )

        lineTo(titleRect.right, titleRect.bottom)

        lineTo(titleRect.left, titleRect.bottom)

        lineTo(titleRect.left, titleRect.top + cornerRadius)

        arcTo(
            rect = Rect(
                left = titleRect.left,
                top = titleRect.top,
                right = titleRect.left + 2 * cornerRadius,
                bottom = titleRect.top + 2 * cornerRadius
            ),
            startAngleDegrees = 180f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        close()
    }
    drawPath(
        path = titlePath,
        color = BlockTitleColor,
        style = Fill
    )

    drawPins(block, block.inBlockPin, block.leftPins, block.inputPins, InOutPinType.INPUT, textMeasurer, density)
    drawPins(block, block.outBlockPin, block.rightPins, block.outputPins, InOutPinType.OUTPUT, textMeasurer, density)

    val textLayout = textMeasurer.measure(
        text = block.title,
        style = TextStyle(
            color = BlockTitleTextColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    )
    val textX = block.x + block.layout.titleArea.left +
            (block.layout.titleArea.width - textLayout.size.width) / 2
    val textY = block.y + block.layout.titleArea.height / 2 - textLayout.size.height / 2

    drawText(
        textLayoutResult = textLayout,
        topLeft = Offset(textX, textY)
    )

    drawRoundRect(
        color = BlockBorderColor,
        topLeft = Offset(block.x, block.y),
        size = Size(block.width, block.height),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius),
        style = Stroke(width = borderThickness)
    )
}

fun DrawScope.drawPins(
    block: BlueBlock,
    blockPin: Pin?,
    pinsCoordinates: List<Offset>,
    logicPins: List<Pin>,
    type: InOutPinType,
    textMeasurer: TextMeasurer,
    density: Density
) {
    if (block.logicBlock is FunctionDefinitionBlock) {
    }

    val pinRadius = PIN_RADIUS_DP.toPx()
    val textPadding = PIN_TEXT_PADDING_DP.toPx()

    if (blockPin != null  && pinsCoordinates.isNotEmpty()) {
        val firstPin = pinCreator.createPin(
            pinsCoordinates[0],
            block,
            type,
            blockPin
        )
        drawBlockPin(firstPin)

        if (block.inBlockPin != null) {
            val pinName = block.inBlockPin.name

            if (pinName.isNotEmpty()) {
                val textStyle = TextStyle(
                    color = PinTextColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
                val textLayout = textMeasurer.measure(
                    text = pinName,
                    style = textStyle
                )

                val textX: Float
                val textY: Float = pinsCoordinates[0].y - textLayout.size.height / 2

                if (type == InOutPinType.INPUT) {
                    textX = pinsCoordinates[0].x + pinRadius + textPadding
                } else {
                    textX = pinsCoordinates[0].x - pinRadius - textPadding - textLayout.size.width
                }

                drawText(
                    textLayoutResult = textLayout,
                    topLeft = Offset(block.x + textX, block.y + textY)
                )
            }
        }
    }

    for (i in logicPins.indices) {
        if (i + 1 < pinsCoordinates.size) {
            val pin = pinCreator.createPin(
                pinsCoordinates[i + 1],
                block,
                type,
                logicPins[i]
            )
            drawPin(pin)

            val pinName = logicPins[i].name
            if (pinName.isNotEmpty()) {
                val textStyle = TextStyle(
                    color = PinTextColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
                val textLayout = textMeasurer.measure(
                    text = pinName,
                    style = textStyle
                )

                val textX: Float
                val textY: Float = pinsCoordinates[i + 1].y - textLayout.size.height / 2

                if (type == InOutPinType.INPUT) {
                    textX = pinsCoordinates[i + 1].x + pinRadius + textPadding
                } else {
                    textX = pinsCoordinates[i + 1].x - pinRadius - textPadding - textLayout.size.width
                }

                drawText(
                    textLayoutResult = textLayout,
                    topLeft = Offset(block.x + textX, block.y + textY)
                )
            }
        }
    }
}