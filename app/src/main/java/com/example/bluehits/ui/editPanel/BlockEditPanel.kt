package com.example.bluehits.ui.editPanel

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.bluehits.ui.BlueBlock
import com.example.bluehits.ui.BlocksManager
import com.example.interpreter.models.FunctionManager
import com.example.interpreter.models.Id
import com.example.interpreter.models.Pin
import com.example.interpreter.models.PinManager

@Immutable
data class PinEditField(
    val pin: Pin,
    val value: Any,
    val isInput: Boolean,
) {
    fun withNewValue(newValue: Any): PinEditField {
        return this.copy(value = newValue)
    }
}

data class BlockEditState(
    val blockId: Id,
    val pinFields: List<PinEditField>,
    var isVisible: Boolean = false
)

@Composable
fun BlockEditPanel(
    modifier: Modifier = Modifier,
    blocksManager: BlocksManager
) {
    val state = BlockEditManager.editState ?: return
    val transition = updateTransition(targetState = state.isVisible, label = "editPanelTransition")
    val alpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) },
        label = "alpha"
    ) { visible -> if (visible) 1f else 0f }

    var showTypeDialog by remember { mutableStateOf(false) }
    var showNameDialog by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("") }
    val block = blocksManager.uiBlocks.find { it.id == state.blockId }

    if (state.isVisible) {
        Box(
            modifier = Modifier.fillMaxSize().zIndex(100f),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { BlockEditManager.hideEditPanel() })
                    }
            )
            Surface(
                modifier = modifier
                    .widthIn(max = 400.dp)
                    .wrapContentHeight()
                    .alpha(alpha),
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                color = Color(0xFF333333),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Edit Pins",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )

                    if (block?.title?.startsWith("def ") == true) {
                        Button(
                            onClick = { showTypeDialog = true },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                        ) {
                            Text("+ Add Pin", color = Color.White)
                        }
                    }

                    if (state.pinFields.isEmpty()) {
                        Text(
                            text = "Нет пинов для редактирования",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
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

        if (showTypeDialog) {
            EditPinTypeDialog(
                onDismiss = { showTypeDialog = false },
                onConfirm = { pinType ->
                    selectedType = pinType
                    showTypeDialog = false
                    showNameDialog = true
                }
            )
        }

        if (showNameDialog && block != null) {
            EditPinNameDialog(
                onDismiss = { showNameDialog = false },
                onConfirm = { pinName ->
                    val pin = when (selectedType) {
                        "Int" -> PinManager.createPinInt(pinName)
                        "Float" -> PinManager.createPinFloat(pinName)
                        "String" -> PinManager.createPinString(pinName)
                        "Boolean" -> PinManager.createPinBool(pinName)
                        else -> PinManager.createPinAny(pinName)
                    }
                    val functionName = block.title.removePrefix("def ").trim()
                    FunctionManager.addFunctionInArg(functionName, pin)
                    // Создаём новый BlueBlock с обновлённым списком inputPins
                    val updatedInputPins = mutableListOf<Pin>().apply {
                        addAll(block.inputPins)
                        add(pin)
                    }
                    val newBlock = BlueBlock(
                        id = block.id,
                        initialX = block.x,
                        initialY = block.y,
                        color = block.color,
                        title = block.title,
                        inputPins = updatedInputPins,
                        outputPins = block.outputPins,
                        inBlockPin = block.inBlockPin,
                        outBlockPin = block.outBlockPin,
                        functionName = block.functionName
                    )
                    // Обновляем блок через метод updateBlock в BlocksManager
                    blocksManager.updateBlock(block.id, newBlock)
                    BlockEditManager.showEditPanel(newBlock) // Refresh panel with new block
                    showNameDialog = false
                }
            )
        }
    }
}

@Composable
fun EditPinTypeDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Pin Type") },
        text = { Text("Choose the type for the new pin:") },
        confirmButton = {
            Column {
                TextButton(onClick = { onConfirm("Int") }) { Text("Int") }
                TextButton(onClick = { onConfirm("Float") }) { Text("Float") }
                TextButton(onClick = { onConfirm("String") }) { Text("String") }
                TextButton(onClick = { onConfirm("Boolean") }) { Text("Boolean") }
                TextButton(onClick = { onConfirm("Any") }) { Text("Any") }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun EditPinNameDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var pinName by remember { mutableStateOf("NewPin") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter Pin Name") },
        text = {
            Column {
                Text("Enter a name for the new pin:")
                OutlinedTextField(
                    value = pinName,
                    onValueChange = { pinName = it },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(pinName) }) { Text("Confirm") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}