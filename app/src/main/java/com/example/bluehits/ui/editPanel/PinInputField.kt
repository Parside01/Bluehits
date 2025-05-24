package com.example.bluehits.ui.editPanel

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun PinInputField(
    filedPin: PinEditField,
    onValueChange: (Any) -> Unit,
    value: Any,
) {
    when {
        filedPin.pin.getType() == Int::class -> {
            NumberInputController(
                initialValue = value.toString().toIntOrNull() ?: 0,
                onValueChange = onValueChange,
                isInt = true
            )
        }
        filedPin.pin.getType() == Float::class -> {
            NumberInputController(
                initialValue = value.toString().toIntOrNull() ?: 0,
                onValueChange = onValueChange,
                isInt = false
            )
        }
        filedPin.pin.getType() == Boolean::class -> {
            BooleanInputField(
                value = value,
                onValueChange = onValueChange
            )
        }
        filedPin.pin.getType() == String::class -> {
            TextInputField(
                value = value,
                onValueChange = onValueChange
            )
        }
        else -> {
            TextInputField(
                value = value,
                onValueChange = onValueChange,
                placeholder = "Enter ${filedPin.pin.getType().simpleName} value"
            )
        }
    }
}

@Composable
private fun NumberInputController(
    initialValue: Number,
    onValueChange: (Number) -> Unit,
    isInt: Boolean
) {
    val intValue = remember { mutableIntStateOf(initialValue.toInt()) }

    val doubleValue = remember { mutableStateOf(initialValue.toDouble()) }

    val step = remember { mutableIntStateOf(1) }

    val isManualInput = remember { mutableStateOf(false) }

    val textValue = remember { mutableStateOf(initialValue.toString()) }

    val currentValue = if (isInt) intValue.intValue else doubleValue.value

    fun applyValue(newValue: Number) {
        if (isInt) {
            intValue.intValue = newValue.toInt()
        } else {
            doubleValue.value = newValue.toDouble()
        }
        textValue.value = newValue.toString()
        onValueChange(newValue)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    val newVal = if (isInt) {
                        intValue.intValue - step.intValue
                    } else {
                        doubleValue.value - step.intValue
                    }
                    applyValue(newVal)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("-")
            }

            if (isManualInput.value) {
                OutlinedTextField(
                    value = textValue.value,
                    onValueChange = { newText ->
                        if (newText.isEmpty() || newText.matches(Regex(if (isInt) "-?\\d*" else "-?\\d*\\.?\\d*"))) {
                            textValue.value = newText
                            val parsed = if (isInt) newText.toIntOrNull() else newText.toDoubleOrNull()
                            parsed?.let { applyValue(it) }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { isManualInput.value = false }
                    ),
                    modifier = Modifier.weight(2f),
                    singleLine = true
                )
            } else {
                Box(
                    modifier = Modifier
                        .weight(2f)
                        .height(48.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        .clickable { isManualInput.value = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = currentValue.toString())
                }
            }

            Button(
                onClick = {
                    val newVal = if (isInt) {
                        intValue.intValue + step.intValue
                    } else {
                        doubleValue.value + step.intValue
                    }
                    applyValue(newVal)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("+")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { step.intValue = (step.intValue - 1).coerceAtLeast(1) },
                modifier = Modifier.weight(1f),
            ) {
                Text("-")
            }

            Box(
                modifier = Modifier
                    .weight(2f)
                    .height(48.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = step.intValue.toString())
            }

            Button(
                onClick = { step.intValue += 1 },
                modifier = Modifier.weight(1f)
            ) {
                Text("+")
            }
        }
    }
}

@Composable
private fun BooleanInputField(
    value: Any,
    onValueChange: (Any) -> Unit
) {
    val checked = value.toString().toBooleanStrictOrNull() ?: false

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Value:", modifier = Modifier.padding(end = 8.dp))
        Switch(
            checked = checked,
            onCheckedChange = { onValueChange(it) }
        )
        Text(if (checked) "True" else "False", modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun TextInputField(
    value: Any,
    onValueChange: (String) -> Unit,
    placeholder: String = "Enter value"
) {
    OutlinedTextField(
        value = value.toString(),
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = { Text(placeholder) }
    )
}