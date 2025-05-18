package com.example.bluehits.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.interpreter.models.Id
import com.example.interpreter.models.Pin


class PinUi(
    var ownOffset: Offset,
    var parentBlock: BlueBlock,
    var type: InOutPinType,
    val id: Id
)

object pinCreator {
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

    fun DrawScope.drawPin(pin: PinUi) {
        if (pin.type == InOutPinType.INPUT) {
            drawCircle(
                color = Color.Green,
                radius = 15f,
                center = Offset(
                    pin.ownOffset.x + pin.parentBlock.x,
                    pin.ownOffset.y + pin.parentBlock.y
                ),
                style = Fill
            )
            drawCircle(
                color = Color.Black,
                radius = 15f,
                center = Offset(
                    pin.ownOffset.x + pin.parentBlock.x,
                    pin.ownOffset.y + pin.parentBlock.y
                ),
                style = Stroke(width = 1.5f)
            )
            return
        }

        drawCircle(
            color = Color.Red,
            radius = 15f,
            center = Offset(
                pin.ownOffset.x + pin.parentBlock.x,
                pin.ownOffset.y + pin.parentBlock.y
            ),
            style = Fill
        )
        drawCircle(
            color = Color.Black,
            radius = 15f,
            center = Offset(
                pin.ownOffset.x + pin.parentBlock.x,
                pin.ownOffset.y + pin.parentBlock.y
            ),
            style = Stroke(width = 1.5f)
        )
    }
}

enum class InOutPinType {
    INPUT,
    OUTPUT
}