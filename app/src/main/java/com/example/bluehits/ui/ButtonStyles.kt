package com.example.bluehits.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlin.math.min
import com.example.bluehits.ui.theme.*

object ButtonStyles {
    data class ButtonStyle(
        val modifier: Modifier = Modifier,
        val shape: RoundedCornerShape = RoundedCornerShape(0.dp),
        val colors: androidx.compose.material3.ButtonColors,
        val contentPadding: PaddingValues,
        val border: BorderStroke,
        val textStyle: TextStyle
    )

    @Composable
    fun baseButtonStyle(): ButtonStyle {
        val config = LocalConfiguration.current
        val baseDimension = min(config.screenWidthDp, config.screenHeightDp).dp
        val buttonWidth = (baseDimension * 0.2f).coerceIn(60.dp, 100.dp)
        val buttonHeight = buttonWidth * 0.5f
        val cornerRadius = buttonWidth * 0.05f

        return ButtonStyle(
            modifier = Modifier
                .size(width = buttonWidth, height = buttonHeight)
                .shadow(4.dp, shape = RoundedCornerShape(cornerRadius)),
            shape = RoundedCornerShape(cornerRadius),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = BaseButtonColor
            ),
            contentPadding = PaddingValues(
                horizontal = buttonWidth * 0.2f,
                vertical = buttonHeight * 0.1f
            ),
            border = BorderStroke(1.dp, ButtonBorder),
            textStyle = TextStyle(
                color = ButtonStyleTextColor,
                fontSize = (buttonHeight.value * 0.4f).sp,
                textAlign = TextAlign.Center
            )
        )
    }

    @Composable
    fun controlPanelButtonStyle(): ButtonStyle {
        val config = LocalConfiguration.current
        val baseDimension = min(config.screenWidthDp, config.screenHeightDp).dp

        val buttonWidth = (baseDimension * 0.8f).coerceIn(120.dp, 180.dp)
        val buttonHeight = buttonWidth * 0.3f
        val cornerRadius = 8.dp

        return ButtonStyle(
            modifier = Modifier
                .fillMaxWidth()
                .size(width = buttonWidth, height = buttonHeight)
                .shadow(4.dp, shape = RoundedCornerShape(cornerRadius)),
            shape = RoundedCornerShape(cornerRadius),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = ControlPanelButtonStyleContainer
            ),
            contentPadding = PaddingValues(vertical = 8.dp),
            border = BorderStroke(1.dp, ColorForControlPanelButton),
            textStyle = TextStyle(
                color = WhiteClassic,
                fontSize = (buttonHeight.value * 0.4f).coerceAtMost(16f).sp,
                textAlign = TextAlign.Center
            )
        )
    }
}