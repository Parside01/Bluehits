package com.example.bluehits.ui.editPanel

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

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
    val floatValue = remember { mutableStateOf(initialValue.toFloat()) }
    val step = remember { mutableStateOf(if (isInt) 1f else 0.1f) }
    val isManualValueInput = remember { mutableStateOf(false) }
    val isManualStepInput = remember { mutableStateOf(false) }
    val valueText = remember { mutableStateOf(initialValue.toString()) }
    val stepText = remember { mutableStateOf(step.value.toString()) }

    fun applyValue(newValue: Number) {
        if (isInt) {
            intValue.intValue = newValue.toInt()
            valueText.value = intValue.intValue.toString()
            onValueChange(intValue.intValue)
        } else {
            floatValue.value = newValue.toFloat()
            valueText.value = floatValue.value.toString()
            onValueChange(floatValue.value)
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    applyValue(
                        if (isInt) intValue.intValue - step.value.toInt()
                        else floatValue.value - step.value
                    )
                },
                modifier = Modifier
                    .height(48.dp)
                    .padding(end = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("-", color = Color.White)
            }

            if (isManualValueInput.value) {
                OutlinedTextField(
                    value = valueText.value,
                    onValueChange = { newText ->
                        if (newText.isEmpty() || newText.matches(Regex(if (isInt) "-?\\d*" else "-?\\d*\\.?\\d*"))) {
                            valueText.value = newText
                            val parsed = if (isInt) newText.toIntOrNull() else newText.toFloatOrNull()
                            parsed?.let { applyValue(it) }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { isManualValueInput.value = false }
                    ),
                    modifier = Modifier
                        .height(48.dp)
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    shape = RoundedCornerShape(8.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .clickable { isManualValueInput.value = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isInt) intValue.intValue.toString()
                        else "%.2f".format(floatValue.value),
                        color = Color.White
                    )
                }
            }

            Button(
                onClick = {
                    applyValue(
                        if (isInt) intValue.intValue + step.value.toInt()
                        else floatValue.value + step.value
                    )
                },
                modifier = Modifier
                    .height(48.dp)
                    .padding(start = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("+", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Step size:", color = Color.White)
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    step.value = (step.value - (if (isInt) 1f else 0.01f)).coerceAtLeast(if (isInt) 1f else 0.01f)
                    stepText.value = step.value.toString()
                },
                modifier = Modifier
                    .height(48.dp)
                    .padding(end = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("-", color = Color.White)
            }

            if (isManualStepInput.value) {
                OutlinedTextField(
                    value = stepText.value,
                    onValueChange = { newText ->
                        if (newText.isEmpty() || newText.matches(Regex(if (isInt) "\\d*" else "\\d*\\.?\\d*"))) {
                            stepText.value = newText
                            val newStep = newText.toFloatOrNull()?.coerceAtLeast(
                                if (isInt) 1f else 0.1f
                            ) ?: (if (isInt) 1f else 0.1f)
                            step.value = newStep
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { isManualStepInput.value = false }
                    ),
                    modifier = Modifier
                        .height(48.dp)
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    shape = RoundedCornerShape(8.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .clickable { isManualStepInput.value = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isInt) step.value.toInt().toString()
                        else "%.1f".format(step.value),
                        color = Color.White
                    )
                }
            }

            Button(
                onClick = {
                    step.value += if (isInt) 1f else 0.1f
                    stepText.value = step.value.toString()
                },
                modifier = Modifier
                    .height(48.dp)
                    .padding(start = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("+", color = Color.White)
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