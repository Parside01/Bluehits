package com.example.bluehits.ui.editPanel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun PinEditRow(
    fieldPin: PinEditField,
    onValueChange: (Any) -> Unit
) {
    val currentValue = fieldPin.pin.getStringValue()

    Column {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = when {
                            fieldPin.isInput -> Color.Green
                            else -> Color.Red
                        },
                        shape = CircleShape
                    )
                    .border(
                        width = 1.5.dp,
                        color = Color.Black,
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = fieldPin.pin.name,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        PinInputField(filedPin = fieldPin, onValueChange = { newValue ->
            onValueChange(newValue)
            BlockEditManager.updatePinValue(fieldPin.pin, newValue)
        }, value=currentValue)
    }
}