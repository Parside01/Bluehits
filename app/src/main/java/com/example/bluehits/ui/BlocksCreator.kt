package com.example.bluehits.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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

    private var currentBlockType: String? = null
    private var onTypeSelected: ((DataType) -> Unit)? = null

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
            "Index", "Append", "Swap", "Array", "Add", "Sub", "Greator" -> {
                currentBlockType = type
                _showTypeDialog.value = true
            }
            "FunctionDefinition" -> showFunctionNameDialog("Define Function")
            "FunctionCall" -> showFunctionNameDialog("Call Function")
            "FunctionReturn" -> showFunctionNameDialog("Return From Function")
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
                "Define Function" -> BlockManager.createFunctionDefinitionBlock(name)
                "Call Function" -> BlockManager.createFunctionCalledBlock(name)
                "Return From Function" -> BlockManager.createFunctionReturnBlock(name)
                else -> throw IllegalArgumentException("Unknown function dialog type")
            }
            _uiBlocks.add(BlockAdapter.wrapLogicBlock(logicBlock))
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
                    DataType.INT -> BlockManager.createAddBlock(type = Int::class)
                    DataType.FLOAT -> BlockManager.createAddBlock(type = Float::class)
                    DataType.DOUBLE -> BlockManager.createAddBlock(type = Double::class)
                    DataType.LONG -> BlockManager.createAddBlock(type = Long::class)
                }
                "Greator" ->  when (type) {
                    DataType.INT -> BlockManager.createAddBlock(type = Int::class)
                    DataType.FLOAT -> BlockManager.createAddBlock(type = Float::class)
                    DataType.DOUBLE -> BlockManager.createAddBlock(type = Double::class)
                    DataType.LONG -> BlockManager.createAddBlock(type = Long::class)
                }
                else -> throw IllegalArgumentException("Unsupported type")
            }
            _uiBlocks.add(BlockAdapter.wrapLogicBlock(logicBlock))
        }
    }

    private fun createBlockWithoutType(type: String) {
        val logicBlock = when (type) {
            "For" -> BlockManager.createForBlock()
            "Int" -> BlockManager.createIntBlock()
            "Bool" -> BlockManager.createBoolBlock()
            "Float" -> BlockManager.createFloatBlock()
            "Print" -> BlockManager.createPrintBlock()
            "IfElse" -> BlockManager.createIfElseBlock()
            else -> throw IllegalArgumentException("Unsupported type")
        }
        _uiBlocks.add(BlockAdapter.wrapLogicBlock(logicBlock))
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
            title = mainLogicBlock.name ?: "Block",
            inputPins = mainLogicBlock.inputs,
            outputPins = mainLogicBlock.outputs,
            blockPin = mainLogicBlock.blockPin,
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
        pinIds.add(block.blockPin.id)
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
                pinIds.add(block.blockPin.id)
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
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Select data type",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DataType.values().forEach { type ->
                Button(
                    onClick = { onTypeSelected(type) },
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
    onNameEntered: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var functionName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = functionName,
                onValueChange = { functionName = it },
                label = { Text("Function name") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (functionName.isNotBlank()) {
                        onNameEntered(functionName)
                    }
                },
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