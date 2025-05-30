package com.example.bluehits.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx. compose. ui. graphics. Path
import com.example.bluehits.ui.theme.BezierLineColor

class LineCreator {
    fun DrawScope.drawBezierCurve(pin1: PinUi, pin2 : PinUi) {
        val startPoint =
            Offset(pin1.parentBlock.x + pin1.ownOffset.x, pin1.parentBlock.y + pin1.ownOffset.y)
        val endPoint =
            Offset(pin2.parentBlock.x + pin2.ownOffset.x, pin2.parentBlock.y + pin2.ownOffset.y)

        val controlPoint1 = lerp(startPoint, endPoint, 0.3f) - Offset(100f, 0f)
        val controlPoint2 = lerp(startPoint, endPoint, 0.7f) + Offset(100f, 0f)

        val path = Path().apply {
            moveTo(startPoint.x, startPoint.y)
            cubicTo(
                controlPoint1.x, controlPoint1.y,
                controlPoint2.x, controlPoint2.y,
                endPoint.x, endPoint.y
            )
        }
        drawPath(path, color = BezierLineColor, style = Stroke(width = 5f))
    }
}