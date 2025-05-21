package com.example.bluehits.ui.editPanel

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            NumberInputField(
                value = value,
                onValueChange = onValueChange,
                isInt = true
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
private fun NumberInputField(
    value: Any,
    onValueChange: (Any) -> Unit,
    isInt: Boolean
) {
    OutlinedTextField(
        value = value.toString(),
        onValueChange = { newValue ->
            if (newValue.isEmpty() || newValue.matches(Regex(if (isInt) "-?\\d+" else "-?\\d*\\.?\\d*"))) {
                val number = if (isInt) {
                    newValue.toIntOrNull()
                } else {
                    newValue.toDoubleOrNull()
                }
                if (number != null) {
                    onValueChange(number)
                }
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = if (isInt) KeyboardType.Number else KeyboardType.NumberPassword
        ),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = { Text(if (isInt) "Enter integer" else "Enter number") }
    )
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