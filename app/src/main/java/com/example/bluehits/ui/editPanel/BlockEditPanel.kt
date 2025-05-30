package com.example.bluehits.ui.editPanel

import android.content.Context
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
import androidx.core.content.ContextCompat.getString
import com.example.bluehits.ui.BlueBlock
import com.example.bluehits.ui.BlocksManager
import com.example.bluehits.ui.DataType
import com.example.bluehits.ui.UIPinManager
import com.example.interpreter.models.BlockManager
import com.example.interpreter.models.FunctionManager
import com.example.interpreter.models.Id
import com.example.interpreter.models.Pin
import com.example.interpreter.models.PinManager
import com.example.bluehits.R

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
    blocksManager: BlocksManager,
    context: Context
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
    var showArrayTypeDialog by remember { mutableStateOf(false) }
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
                color = BlockEditPanelSurface,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = getString(context, R.string.change_pins),
                        style = MaterialTheme.typography.titleMedium,
                        color = WhiteClassic
                    )

                    if (block?.title?.startsWith("def ") == true || block?.title?.startsWith("return ") == true) {
                        Button(
                            onClick = { showTypeDialog = true },
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BlockEditPanelButtonContainer,
                                contentColor = BlockEditPanelButtonContent
                            )
                        ) {
                            Text(getString(context, R.string.add_new_pin), color = WhiteClassic)
                        }
                    }

                    if (!state.pinFields.isEmpty()) {
                        state.pinFields.forEach { field ->
                            PinEditRow(
                                fieldPin = field,
                                onValueChange = { newValue ->
                                    BlockEditManager.updatePinValue(field.pin, newValue)
                                },
                                context = context
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
                },
                onArrayTypeSelected = {
                    showTypeDialog = false
                    showArrayTypeDialog = true
                },
                context=context
            )
        }

        if (showArrayTypeDialog) {
            ArrayElementTypeDialog(
                onTypeSelected = { dataType ->
                    selectedType = "Array<${dataType.title}>"
                    showArrayTypeDialog = false
                    showNameDialog = true
                },
                onDismiss = {
                    showArrayTypeDialog = false
                    showTypeDialog = true
                },
                context=context
            )
        }

        if (showNameDialog && block != null) {
            EditPinNameDialog(
                onDismiss = { showNameDialog = false },
                onConfirm = { pinName ->
                    val pin = when {
                        selectedType.startsWith("Array<") -> {
                            val elementType = selectedType.removePrefix("Array<").removeSuffix(">")
                            when (elementType) {
                                getString(context, R.string.int_block_label) -> PinManager.createPinArray(pinName, ownId = state.blockId, elementType = Int::class)
                                getString(context, R.string.float_block_label) -> PinManager.createPinArray(pinName, ownId = state.blockId, elementType = Float::class)
                                getString(context, R.string.double_block_label) -> PinManager.createPinArray(pinName, ownId = state.blockId, elementType = Double::class)
                                getString(context, R.string.long_block_label) -> PinManager.createPinArray(pinName, ownId = state.blockId, elementType = Long::class)
                                else -> PinManager.createPinArray(pinName, ownId = state.blockId, elementType = Any::class)
                            }
                        }
                        selectedType == getString(context, R.string.int_block_label) -> PinManager.createPinInt(pinName, ownId = state.blockId)
                        selectedType == getString(context, R.string.float_block_label) -> PinManager.createPinFloat(pinName, ownId = state.blockId)
                        selectedType == getString(context, R.string.string_block_label) -> PinManager.createPinString(pinName, ownId = state.blockId)
                        selectedType == getString(context, R.string.bool_block_label) -> PinManager.createPinBool(pinName, ownId = state.blockId)
                        else -> PinManager.createPinAny(pinName, ownId = state.blockId)
                    }

                    // TODO: можно просто кастить и делать getFuncName
                    val functionName = block.title.removePrefix("def ").removePrefix("return ").trim()

                    if (block.title.startsWith("def ")) {
                        FunctionManager.addFunctionInArg(functionName, pin)
                    } else if (block.title.startsWith("return ")) {
                        FunctionManager.addFunctionOutArg(functionName, pin)
                    }

                    val newBlock = BlueBlock(
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
                    blocksManager.updateBlock(block.id, newBlock)
                    BlockEditManager.showEditPanel(newBlock)
                    showNameDialog = false
                    showTypeDialog = false
                },
                context=context
            )
        }
    }
}

@Composable
fun EditPinTypeDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    onArrayTypeSelected: () -> Unit,
    context: Context
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(EditPinTypeDialogBackground, RoundedCornerShape(12.dp))
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = getString(context, R.string.choose_pin_type),
                style = MaterialTheme.typography.titleMedium,
                color = WhiteClassic,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            listOf(getString(context, R.string.int_block_label), getString(context, R.string.float_block_label), getString(context, R.string.string_block_label), "Boolean", getString(context, R.string.array_block_label), "Any").forEach { type ->
                Button(
                    onClick = { if (type == getString(context, R.string.array_block_label)) {
                        onArrayTypeSelected()
                    } else {
                        onConfirm(type)
                    } },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EditPinTypeDialogContainer,
                        contentColor = EditPinTypeDialogContent
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
    onConfirm: (String) -> Unit,
    context: Context
) {
    var pinName by remember { mutableStateOf(getString(context, R.string.new_pin_default_name)) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(DarkBackground, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = pinName,
                onValueChange = { pinName = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = WhiteClassic),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = WhiteClassic,
                    unfocusedTextColor = WhiteClassic,
                    cursorColor = WhiteClassic,
                    focusedBorderColor = LightGrayClassic,
                    unfocusedBorderColor = GrayClassic
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
                colors = ButtonDefaults.buttonColors(containerColor = EditPinNameDialogContainer, contentColor = EditPinNameDialogContent),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(getString(context, R.string.create_button_text))
            }
        }
    }
}

@Composable
fun ArrayElementTypeDialog(
    onTypeSelected: (DataType) -> Unit,
    onDismiss: () -> Unit,
    context: Context
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(DarkBackground, RoundedCornerShape(12.dp))
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = getString(context, R.string.select_array_elem_type),
                style = MaterialTheme.typography.titleMedium,
                color = WhiteClassic,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DataType.values().forEach { type ->
                Button(
                    onClick = { onTypeSelected(type) },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ArrayElementTypeDialogContainer,
                        contentColor = ArrayElementTypeDialogContent
                    )
                ) {
                    Text(text = type.title)
                }
            }
        }
    }
}