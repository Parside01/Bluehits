package com.example.bluehits.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke

class PinUi(var ownOffset: Offset,
    var parentBlock: BlueBlock,
    var type: String)

object pinCreator {
    fun createPin(ownOffset: Offset, parentBlock: BlueBlock, type: String): PinUi {
        return PinUi(ownOffset, parentBlock, type).also {
            PinManager.addPin(it)
        }
    }

    fun DrawScope.drawPin(pin: PinUi) {
        if (pin.type == "Input") {
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
        } else {
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
}