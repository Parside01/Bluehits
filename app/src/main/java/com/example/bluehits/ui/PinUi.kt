package com.example.bluehits.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.bluehits.ui.theme.*
import com.example.interpreter.models.Id
import com.example.interpreter.models.Pin

class PinUi(
    var ownOffset: Offset,
    var parentBlock: BlueBlock,
    var type: InOutPinType,
    val id: Id
)

object pinCreator {
    private val typeColorMap = mapOf(
        Int::class to PinInt,
        String::class to PinString,
        Boolean::class to PinBoolean,
        Float::class to PinFloat,
        Double::class to PinDouble,
        Long::class to PinLong,
        Any::class to PinAny,
        List::class to PinList
    )

    fun createPin(
        ownOffset: Offset,
        parentBlock: BlueBlock,
        type: InOutPinType,
        logicPin: Pin
    ): PinUi {
        return PinUi(ownOffset, parentBlock, type, id = logicPin.id).also {
            UIPinManager.addPin(it)
        }
    }

    fun DrawScope.drawPin(pin: PinUi, isSelected: Boolean = false) {
        val borderWidth = if (isSelected) 2.5f else 1.5f
        val borderColor = if (isSelected) SelectedPin else PinBorder

        val pinColor = typeColorMap[pin.parentBlock.inputPins.find { it.id == pin.id }?.getType()]
            ?: typeColorMap[pin.parentBlock.outputPins.find { it.id == pin.id }?.getType()]
            ?: PinAny

        drawCircle(
            color = pinColor,
            radius = 15f,
            center = Offset(
                pin.ownOffset.x + pin.parentBlock.x,
                pin.ownOffset.y + pin.parentBlock.y
            ),
            style = Fill
        )
        drawCircle(
            color = borderColor,
            radius = 15f,
            center = Offset(
                pin.ownOffset.x + pin.parentBlock.x,
                pin.ownOffset.y + pin.parentBlock.y
            ),
            style = Stroke(width = borderWidth)
        )
    }
    fun DrawScope.drawBlockPin(pin: PinUi, isSelected: Boolean = false) {
        val borderWidth = if (isSelected) 2.5f else 1.5f
        val borderColor = if (isSelected) SelectedPin else PinBorder

        val size = 20f
        val halfSize = size / 2

        drawRect(
            color = BlockPin,
            topLeft = Offset(
                pin.ownOffset.x + pin.parentBlock.x - halfSize,
                pin.ownOffset.y + pin.parentBlock.y - halfSize
            ),
            size = Size(size, size),
            style = Fill
        )
        drawRect(
            color = borderColor,
            topLeft = Offset(
                pin.ownOffset.x + pin.parentBlock.x - halfSize,
                pin.ownOffset.y + pin.parentBlock.y - halfSize
            ),
            size = Size(size, size),
            style = Stroke(width = borderWidth)
        )
    }
}

enum class InOutPinType {
    INPUT,
    OUTPUT
}