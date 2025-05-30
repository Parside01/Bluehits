package com.example.bluehits.ui.editPanel

import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlin.reflect.KClass


@Composable
fun PinInputField(
    filedPin: PinEditField,
    onValueChange: (Any) -> Unit,
    value: Any,
) {
    println(filedPin.pin.getType())
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
                initialValue = value.toString().toFloatOrNull() ?: 0f,
                onValueChange = onValueChange,
                isInt = false
            )
        }

        filedPin.pin.getType() == Double::class -> {
            NumberInputController(
                initialValue = value.toString().toDoubleOrNull() ?: 0f,
                onValueChange = onValueChange,
                isInt = false
            )
        }

        filedPin.pin.getType() == Long::class -> {
            NumberInputController(
                initialValue = value.toString().toLongOrNull() ?: 0,
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

        // Один фиг там всегда просто emptyList какая разница какой тип ставить
        filedPin.pin.getType() == emptyList<Int>()::class -> {
            ArrayInputField(
                fieldPin = filedPin,
                onValueChange = onValueChange,
                value = value
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
    val currentValue = remember { mutableStateOf(initialValue) }

    LaunchedEffect(initialValue) {
        currentValue.value = initialValue
    }

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    currentValue.value = if (isInt) currentValue.value.toInt() - step.value.toInt()
                    else currentValue.value.toFloat() - step.value.toFloat()
                    onValueChange(currentValue.value)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White),
                modifier = Modifier
                    .height(48.dp)
                    .padding(end = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("-", color = Color.DarkGray)
            }

            if (isManualValueInput.value) {
                OutlinedTextField(
                    value = currentValue.value.toString(),
                    onValueChange = { newText ->
                        if (newText.isEmpty() || newText.matches(Regex(if (isInt) "-?\\d*" else "-?\\d*\\.?\\d*"))) {
                            val parsed = if (isInt) newText.toIntOrNull() ?: 0 else newText.toFloatOrNull() ?: 0f
                            currentValue.value = parsed
                            onValueChange(currentValue.value)
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
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedBorderColor = Color.LightGray,
                        unfocusedBorderColor = Color.Gray
                    )
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
                        text = if (isInt) currentValue.value.toString()
                        else "%.2f".format(currentValue.value.toDouble()),
                        color = Color.White
                    )
                }
            }

            Button(
                onClick = {
                    currentValue.value = if (isInt) currentValue.value.toInt() + step.value.toInt()
                    else currentValue.value.toFloat() + step.value.toFloat()
                    onValueChange(currentValue.value)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White),
                modifier = Modifier
                    .height(48.dp)
                    .padding(start = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("+", color = Color.DarkGray)
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
                    onValueChange(currentValue.value)
                },
                colors =  ButtonDefaults.buttonColors(
                    containerColor = Color.White),
                modifier = Modifier
                    .height(48.dp)
                    .padding(end = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("-", color = Color.DarkGray)
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
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedBorderColor = Color.LightGray,
                        unfocusedBorderColor = Color.Gray
                    )
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
                        else "%.2f".format(step.value.toDouble()),
                        color = Color.White
                    )
                }
            }

            Button(
                onClick = {
                    step.value += if (isInt) 1f else 0.1f
                    stepText.value = step.value.toString()
                    onValueChange(currentValue.value)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White),
                modifier = Modifier
                    .height(48.dp)
                    .padding(start = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("+", color = Color.DarkGray)
            }
        }
    }
}

@Composable
private fun BooleanInputField(
    value: Any,
    onValueChange: (Any) -> Unit
) {
    val checked = remember { mutableStateOf(value.toString().toBooleanStrictOrNull() ?: false) }
    val textColor = Color.White
    val switchColors = SwitchDefaults.colors(
        checkedThumbColor = Color.Black,
        checkedTrackColor = Color.DarkGray,
        uncheckedThumbColor = Color.White,
        uncheckedTrackColor = Color.LightGray,
        checkedBorderColor = Color.Black,
        uncheckedBorderColor = Color.Gray
    )

    LaunchedEffect(value) {
        checked.value = value.toString().toBooleanStrictOrNull() ?: false
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Switch(
            checked = checked.value,
            onCheckedChange = {
                checked.value = it
                onValueChange(it)
            },
            colors = switchColors,
        )
        Text(
            if (checked.value) "True" else "False",
            color = textColor,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun TextInputField(
    value: Any,
    onValueChange: (String) -> Unit,
    placeholder: String = "Enter value"
) {
    val textState = remember { mutableStateOf(value.toString()) }

    OutlinedTextField(
        value = textState.value,
        onValueChange = {
            textState.value = it
            onValueChange(it)
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(color = Color.White),
        placeholder = { Text(placeholder) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White,
            focusedBorderColor = Color.LightGray,
            unfocusedBorderColor = Color.Gray
        )
    )
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ArrayInputField(
    fieldPin: PinEditField,
    onValueChange: (Any) -> Unit,
    value: Any
) {
    val initialList = remember {
        (value as? Array<*>)?.toList() ?: emptyList()
    }
    val items = remember { mutableStateOf(initialList) }
    val scrollState = rememberScrollState()
    val itemType = fieldPin.pin.getType().simpleName
    val elementType = fieldPin.pin.getElementType()
    val selectedIndex = remember { mutableStateOf<Int?>(null) }


    fun createDefaultValue(): Any {
        return when (elementType) {
            Int::class -> 0
            Float::class -> 0f
            Double::class -> 0
            Long::class -> 0
            Boolean::class -> false
            String::class -> ""
            else -> ""
        }
    }
    val state = BlockEditManager.editState ?: return
    val transition = updateTransition(targetState = state.isVisible, label = "editPanelTransition")

    LaunchedEffect(value) {
        items.value = (value as? Array<*>)?.toList() ?: emptyList()
    }

    LaunchedEffect(items.value) {
        BlockEditManager.updatePinValue(fieldPin.pin, items.value.toTypedArray())
        onValueChange(items.value.toTypedArray())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 9
        ) {
            items.value.forEachIndexed { index, item ->
                ArrayItemBox(
                    item = item,
                    onValueChange = { newValue ->
                        val newList = items.value.toMutableList()
                        newList[index] = when (elementType) {
                            Int::class -> (newValue as String).toIntOrNull() ?: 0
                            Float::class -> (newValue as String).toFloatOrNull() ?: 0f
                            Double::class -> (newValue as String).toDoubleOrNull() ?: 0
                            Long::class -> (newValue as String).toLongOrNull() ?: 0
                            Boolean::class -> (newValue as String).toBooleanStrictOrNull() ?: false
                            else -> newValue
                        }
                        items.value = newList
                        BlockEditManager.updatePinValue(fieldPin.pin, newList.toTypedArray())
                        onValueChange(newList.toTypedArray())
                    },
                    onRemove = {
                        val newList = items.value.toMutableList()
                        newList.removeAt(index)
                        items.value = newList
                        BlockEditManager.updatePinValue(fieldPin.pin, newList.toTypedArray())
                        onValueChange(newList.toTypedArray())
                    },
                    fieldPin = fieldPin,
                    elementType = elementType,
                    onItemSelected = { selectedIndex.value = index }
                )
            }

            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF3A3A3A))
                    .clickable {
                        val newList = items.value.toMutableList()
                        newList.add(createDefaultValue())
                        items.value = newList
                        BlockEditManager.updatePinValue(fieldPin.pin, newList.toTypedArray())
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("+", color = Color.White, fontSize = 20.sp)
            }
        }
    }

    if (selectedIndex.value != null) {
        val index = selectedIndex.value!!
        EditItemDialog(
            item = items.value[index],
            elementType = elementType,
            onValueChange = { newValue ->
                val newList = items.value.toMutableList().apply {
                    set(index, newValue)
                }
                items.value = newList
                BlockEditManager.updatePinValue(fieldPin.pin, items.value.toTypedArray())
                onValueChange(items.value.toTypedArray())
            },
            onDismiss = { selectedIndex.value = null },
            fieldPin = fieldPin,
            isInt = when (elementType) {
                Int::class, Long::class -> true
                else -> false
            }
        )
    }
}

@Composable
private fun ArrayItemBox(
    item: Any?,
    onValueChange: (Any) -> Unit,
    onRemove: () -> Unit,
    fieldPin: PinEditField,
    elementType: KClass<*>,
    onItemSelected: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF424242))
            .clickable { onItemSelected() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = item?.toString()?.takeIf { it.isNotBlank() } ?: "0",
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

@Composable
fun EditItemDialog(
    item: Any?,
    elementType: KClass<*>,
    onValueChange: (Any) -> Unit,
    onDismiss: () -> Unit,
    fieldPin: PinEditField,
    isInt: Boolean
) {

    val textValue = remember { mutableStateOf(item?.toString() ?: "") }

    val editingValue = remember { mutableStateOf(item) }

    LaunchedEffect(item) {
        editingValue.value = item
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(Color(0xFF424242), RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Edit Item",
                fontSize = 20.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))

            when (elementType) {
                Int::class -> {
                    val intValue = remember { mutableIntStateOf(editingValue.value.toString().toIntOrNull() ?: 0) }
                    val initialValue = intValue.intValue
                    NumberInputController(
                        initialValue = initialValue,
                        onValueChange = { newValue ->
                            onValueChange(newValue)
                        },
                        isInt = isInt
                    )
                }

                Float::class -> {
                    val initialValue = item?.toString()?.toFloatOrNull() ?: 0f
                    NumberInputController(
                        initialValue = initialValue,
                        onValueChange = { newValue ->
                            onValueChange(newValue.toFloat())
                        },
                        isInt = isInt
                    )
                }

                Double::class -> {
                    val initialValue = item?.toString()?.toDoubleOrNull() ?: 0
                    NumberInputController(
                        initialValue = initialValue,
                        onValueChange = { newValue ->
                            onValueChange(newValue.toDouble())
                        },
                        isInt = isInt
                    )
                }

                Long::class -> {
                    val initialValue = item?.toString()?.toLongOrNull() ?: 0f
                    NumberInputController(
                        initialValue = initialValue,
                        onValueChange = { newValue ->
                            onValueChange(newValue.toLong())
                        },
                        isInt = isInt
                    )
                }

                Boolean::class -> {
                    val initialValue = item?.toString()?.toBooleanStrictOrNull() ?: false
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Value: ", color = Color.White)
                        Switch(
                            checked = initialValue,
                            onCheckedChange = { newValue ->
                                onValueChange(newValue)
                            }
                        )
                    }
                }

                String::class -> {
                    TextInputField(
                        value = item?.toString() ?: "",
                        onValueChange = { textValue.value = it }
                    )
                }

                else -> {
                    Text("Unsupported type", color = Color.Black)
                }
            }
        }
    }
}

