package com.example.bluehits.ui.editPanel

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.interpreter.models.Id
import com.example.interpreter.models.Pin

@Immutable
data class PinEditField(
    val pin: Pin,
    val value: Any,
    val isInput: Boolean,
) {
    fun withNewValue(newValue: Any): PinEditField {
        return this.copy(
            value = newValue,
        )
    }
}

data class BlockEditState(
    val blockId: Id,
    val pinFields: List<PinEditField>,
    var isVisible: Boolean = false
)

@Composable
fun BlockEditPanel(
    modifier: Modifier = Modifier
) {
    val state = BlockEditManager.editState ?: return
    val transition = updateTransition(targetState = state.isVisible, label = "editPanelTransition")

    val alpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) },
        label = "alpha"
    ) { visible ->
        if (visible) 1f else 0f
    }

    if (state.isVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(100f),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            BlockEditManager.hideEditPanel()
                        })
                    }
            )
            Surface(
                modifier = modifier
                    .widthIn(max = 400.dp)
                    .wrapContentHeight()
                    .alpha(alpha),
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                color = Color(0xFF333333),
                shadowElevation = 8.dp,
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Edit Pins",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )

                    state.pinFields.forEach { field ->
                        PinEditRow(
                            fieldPin = field,
                            onValueChange = { newValue ->
                                BlockEditManager.updatePinValue(field.pin, newValue)
                            }
                        )
                    }
                }
            }
        }
    }
}