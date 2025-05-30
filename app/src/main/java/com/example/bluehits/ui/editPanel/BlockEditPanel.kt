package com.example.bluehits.ui.editPanel

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import com.example.bluehits.ui.BlueBlock
import com.example.bluehits.ui.BlocksManager
import com.example.bluehits.ui.UIPinManager
import com.example.interpreter.models.BlockManager
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
                        text = "Редактировать пины",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )

                    if (block?.title?.startsWith("def ") == true || block?.title?.startsWith("return ") == true) {
                        Button(
                            onClick = { showTypeDialog = true },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                        ) {
                            Text("+ Добавить пин", color = Color.White)
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
                        "Int" -> PinManager.createPinInt(pinName, ownId = state.blockId)
                        "Float" -> PinManager.createPinFloat(pinName, ownId = state.blockId)
                        "String" -> PinManager.createPinString(pinName, ownId = state.blockId)
                        "Boolean" -> PinManager.createPinBool(pinName, ownId = state.blockId)
                        else -> PinManager.createPinAny(pinName, ownId = state.blockId)
                    }
                    // TODO: можно просто кастить и делать getFuncName
                    val functionName = block.title.removePrefix("def ").removePrefix("return ").trim()

                    if (block.title.startsWith("def ")) {
                        FunctionManager.addFunctionInArg(functionName, pin)
                    } else if (block.title.startsWith("return ")) {
                        FunctionManager.addFunctionOutArg(functionName, pin)
                    }

                    val updatedBlock = BlueBlock(
                        id = block.id,
                        initialX = block.x,
                        initialY = block.y,
                        color = block.color,
                        title = block.title,
                        inputPins = BlockManager.getBlock(block.id)!!.inputs,
                        outputPins = BlockManager.getBlock(block.id)!!.outputs,
                        inBlockPin = block.inBlockPin,
                        outBlockPin = block.outBlockPin,
                        functionName = block.functionName
                    )
                    blocksManager.updateBlock(block.id, updatedBlock)
                    BlockEditManager.showEditPanel(updatedBlock)
                    showNameDialog = false
                    showTypeDialog = false
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
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(Color.DarkGray, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Выберите тип пина",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            listOf("Int", "Float", "String", "Boolean", "Any").forEach { type ->
                Button(
                    onClick = { onConfirm(type) },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black
                    )
                ) {
                    Text(text = type)
                }
            }
        }
    }
}

@Composable
fun EditPinNameDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var pinName by remember { mutableStateOf("NewPin") }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(Color.DarkGray, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = pinName,
                onValueChange = { pinName = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.Gray
                )
            )

            Button(
                onClick = {
                    if (pinName.isNotBlank()) {
                        onConfirm(pinName)
                        BlockEditManager.hideEditPanel()
                    }
                },
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.DarkGray),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Создать")
            }
        }
    }
}