package com.example.bluehits.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.interpreter.models.BlockManager
import com.example.interpreter.models.ConnectionManager
import com.example.interpreter.models.Id
import com.example.interpreter.models.PinManager
import com.example.interpreter.models.Program

class BlocksManager {
    private val _uiBlocks = mutableStateListOf<BlueBlock>()
    val uiBlocks: List<BlueBlock> get() = _uiBlocks
    private var _showTypeDialog = mutableStateOf(false)
    val showTypeDialog: State<Boolean> get() = _showTypeDialog
    private var _showValueNameDialog = mutableStateOf(false)
    val showValueNameDialog: State<Boolean> get() = _showValueNameDialog
    private var currentValueBlockType: String? = null

    private var currentBlockType: String? = null
    private var onTypeSelected: ((DataType) -> Unit)? = null

    private var screenWidthPx by mutableStateOf(0f)
    private var screenHeightPx by mutableStateOf(0f)

    fun updateScreenSize(width: Float, height: Float) {
        screenWidthPx = width
        screenHeightPx = height
    }

    public fun getPrintBlockValue(uiBlocks: List<BlueBlock>): Any? {
        uiBlocks.forEach { block ->
            if (block.title == "Print") {
                val logicBlock = BlockManager.getBlock(block.id)
                return logicBlock?.let { notNullBlock -> notNullBlock.inputs[0].getValue() }
            }
        }
        return null
    }


    fun addNewBlock(type: String) {
        when (type) {
            "Index", "Append", "Swap", "Array", "Add", "Sub", "Greater" -> {
                currentBlockType = type
                _showTypeDialog.value = true
            }
            "Function def" -> showFunctionNameDialog("Function def")
            "Function call" -> showFunctionNameDialog("Function call")
            "Function return" -> showFunctionNameDialog("Function return")
            "Int" -> showFunctionNameDialog("Int")
            "Float" -> showFunctionNameDialog("Float")
            "Bool" -> showFunctionNameDialog("Bool")
            "String" -> showFunctionNameDialog("String")

            else -> createBlockWithoutType(type)
        }
    }

    private var _showFunctionNameDialog = mutableStateOf(false)
    val showFunctionNameDialog: State<Boolean> get() = _showFunctionNameDialog
    var currentFunctionDialogType: String? = null

    private fun showFunctionNameDialog(dialogType: String) {
        currentFunctionDialogType = dialogType
        _showFunctionNameDialog.value = true
    }

    fun onFunctionNameEntered(name: String) {
        _showFunctionNameDialog.value = false
        currentFunctionDialogType?.let { dialogType ->
            val logicBlock = when (dialogType) {
                "Function def" -> BlockManager.createFunctionDefinitionBlock(name)
                "Function call" -> BlockManager.createFunctionCalledBlock(name)
                "Function return" -> BlockManager.createFunctionReturnBlock(name)
                "Int" -> BlockManager.createIntBlock(name)
                "Float" -> BlockManager.createFloatBlock(name)
                "Bool" -> BlockManager.createBoolBlock(name)
                "String" -> BlockManager.createStringBlock(name)
                else -> throw IllegalArgumentException("Unknown function dialog type")
            }

            val centerX = screenWidthPx
            val centerY = screenHeightPx
            _uiBlocks.add(BlockAdapter.wrapLogicBlock(logicBlock, centerX = centerX, centerY = centerY))
        }
    }

    fun dismissFunctionNameDialog() {
        _showFunctionNameDialog.value = false
    }

    fun onTypeSelected(type: DataType) {
        _showTypeDialog.value = false
        currentBlockType?.let { blockType ->
            val logicBlock = when (blockType) {
                "Index" -> when (type) {
                    DataType.INT -> BlockManager.createIndexBlock<Int>()
                    DataType.FLOAT -> BlockManager.createIndexBlock<Float>()
                    DataType.DOUBLE -> BlockManager.createIndexBlock<Double>()
                    DataType.LONG -> BlockManager.createIndexBlock<Long>()
                }
                "Append" -> when (type) {
                    DataType.INT -> BlockManager.createAppendBlock<Int>()
                    DataType.FLOAT -> BlockManager.createAppendBlock<Float>()
                    DataType.DOUBLE -> BlockManager.createAppendBlock<Double>()
                    DataType.LONG -> BlockManager.createAppendBlock<Long>()
                }
                "Swap" -> when (type) {
                    DataType.INT -> BlockManager.createSwapBlock<Int>()
                    DataType.FLOAT -> BlockManager.createSwapBlock<Float>()
                    DataType.DOUBLE -> BlockManager.createSwapBlock<Double>()
                    DataType.LONG -> BlockManager.createSwapBlock<Long>()
                }
                "Array" -> when (type) {
                    DataType.INT -> BlockManager.createArrayBlock<Int>()
                    DataType.FLOAT -> BlockManager.createArrayBlock<Float>()
                    DataType.DOUBLE -> BlockManager.createArrayBlock<Double>()
                    DataType.LONG -> BlockManager.createArrayBlock<Long>()
                }
                "Add" ->  when (type) {
                    DataType.INT -> BlockManager.createAddBlock(type = Int::class)
                    DataType.FLOAT -> BlockManager.createAddBlock(type = Float::class)
                    DataType.DOUBLE -> BlockManager.createAddBlock(type = Double::class)
                    DataType.LONG -> BlockManager.createAddBlock(type = Long::class)
                }
                "Sub" ->  when (type) {
                    DataType.INT -> BlockManager.createSubBlock(type = Int::class)
                    DataType.FLOAT -> BlockManager.createSubBlock(type = Float::class)
                    DataType.DOUBLE -> BlockManager.createSubBlock(type = Double::class)
                    DataType.LONG -> BlockManager.createSubBlock(type = Long::class)
                }
                "Greater" ->  when (type) {
                    DataType.INT -> BlockManager.createGreaterBlock(type = Int::class)
                    DataType.FLOAT -> BlockManager.createGreaterBlock(type = Float::class)
                    DataType.DOUBLE -> BlockManager.createGreaterBlock(type = Double::class)
                    DataType.LONG -> BlockManager.createGreaterBlock(type = Long::class)
                }
                else -> throw IllegalArgumentException("Unsupported type")
            }
            val centerX = screenWidthPx
            val centerY = screenHeightPx
            _uiBlocks.add(BlockAdapter.wrapLogicBlock(logicBlock, centerX = centerX, centerY = centerY))
        }
    }

    private fun createBlockWithoutType(type: String) {
        val logicBlock = when (type) {
            "For" -> BlockManager.createForBlock()
            "Print" -> BlockManager.createPrintBlock()
            "IfElse" -> BlockManager.createIfElseBlock()
            "Math" -> BlockManager.createMathBlock()
            else -> throw IllegalArgumentException("Unsupported type")
        }
        val centerX = screenWidthPx
        val centerY = screenHeightPx
        _uiBlocks.add(BlockAdapter.wrapLogicBlock(logicBlock, centerX = centerX, centerY = centerY))
    }

    fun dismissTypeDialog() {
        _showTypeDialog.value = false
    }

    init {
        createMainBlockInUI()
    }

    fun createMainBlockInUI() {
        val mainLogicBlock = Program.getMainBlock()
        val mainBlueBlock = BlueBlock(
            id = mainLogicBlock.id,
            initialX = 0f,
            initialY = 0f,
            color = Color.Gray,
            title = mainLogicBlock.name,
            inputPins = mainLogicBlock.inputs,
            outputPins = mainLogicBlock.outputs,
            inBlockPin = mainLogicBlock.blockPin,
            outBlockPin = mainLogicBlock.outBlockPin,
        )
        _uiBlocks.add(mainBlueBlock)
    }

    fun moveBlock(block: BlueBlock, delta: Offset) {
        val index = _uiBlocks.indexOf(block)
        if (index != -1) {
            _uiBlocks[index].x += delta.x
            _uiBlocks[index].y += delta.y
        }
    }

    fun removeBlock(block: BlueBlock, connectionManager: UIConnectionManager) {
        val pinIds = mutableListOf<Id>()
        pinIds.add(block.inBlockPin.id)
        block.inputPins.forEach { pinIds.add(it.id) }
        block.outputPins.forEach { pinIds.add(it.id) }
        pinIds.forEach { pinId ->
            PinManager.getPin(pinId)?.let { pin ->
                ConnectionManager.getPinConnections(pin).forEach { connection ->
                    ConnectionManager.disconnect(connection.id.string())
                }
            }
        }
        connectionManager.connections.removeAll { (pin1, pin2) ->
            pinIds.contains(pin1.id) || pinIds.contains(pin2.id)
        }
        UIPinManager.clearPinsForBlock(block)
        _uiBlocks.remove(block)
    }

    fun clearAllBlocks(connectionManager: UIConnectionManager) {
        _uiBlocks.toList().forEach { block ->
            if (block.title != "Main") {
                val pinIds = mutableListOf<Id>()
                pinIds.add(block.inBlockPin.id)
                block.inputPins.forEach { pinIds.add(it.id) }
                block.outputPins.forEach { pinIds.add(it.id) }
                pinIds.forEach { pinId ->
                    PinManager.getPin(pinId)?.let { pin ->
                        ConnectionManager.getPinConnections(pin).forEach { connection ->
                            ConnectionManager.disconnect(connection.id.string())
                        }
                    }
                }
                connectionManager.connections.removeAll { (pin1, pin2) ->
                    pinIds.contains(pin1.id) || pinIds.contains(pin2.id)
                }
                UIPinManager.clearPinsForBlock(block)
                _uiBlocks.remove(block)
            }

        }
    }
}

@Composable
fun TypeSelectionDialog(
    onTypeSelected: (DataType) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(Color.DarkGray, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Select data type",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
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
                        containerColor = Color.LightGray,
                        contentColor = Color.Black
                    )
                ) {
                    Text(text = type.title)
                }
            }
        }
    }
}

@Composable
fun FunctionNameDialog(
    title: String,
    label: String = "Function name",
    onNameEntered: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var functionName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(Color.DarkGray, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = functionName,
                onValueChange = { functionName = it },
                label = { Text(label,
                    color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (functionName.isNotBlank()) {
                            onNameEntered(functionName)
                        }
                    }
                ),
                singleLine = true,
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
                    if (functionName.isNotBlank()) {
                        onNameEntered(functionName)
                    }
                },
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.DarkGray),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Create")
            }
        }
    }
}

enum class DataType(val title: String) {
    INT("Int"),
    FLOAT("Float"),
    DOUBLE("Double"),
    LONG("Long")
}