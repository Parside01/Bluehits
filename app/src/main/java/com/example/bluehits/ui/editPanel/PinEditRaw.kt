package com.example.bluehits.ui.editPanel

import android.content.Context
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
import androidx.compose.ui.unit.dp
import com.example.bluehits.ui.theme.*

@Composable
fun PinEditRow(
    fieldPin: PinEditField,
    onValueChange: (Any) -> Unit,
    context: Context
) {
    val currentValue = fieldPin.pin.getValue()
    val typeColorMap = mapOf(
        Int::class to PinInt,
        String::class to PinString,
        Boolean::class to PinBoolean,
        Float::class to PinFloat,
        Double::class to PinDouble,
        Long::class to PinLong,
        Any::class to PinAny,
        List::class to PinList
    )
    val pinColor = typeColorMap[fieldPin.pin.getType()] ?: PinAny

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = pinColor,
                        shape = CircleShape
                    )
                    .border(
                        width = 1.5.dp,
                        color = PinBorder,
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = fieldPin.pin.name,
                color = BlockPin,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        PinInputField(filedPin = fieldPin, onValueChange = { newValue ->
            onValueChange(newValue)
            BlockEditManager.updatePinValue(fieldPin.pin, newValue)
        }, value = currentValue, context = context)
    }
}