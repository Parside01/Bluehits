package com.example.bluehits.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.getString
import com.example.interpreter.blocks.FunctionPartBlock
import com.example.interpreter.models.BlockManager
import com.example.interpreter.models.ConnectionManager
import com.example.interpreter.models.FunctionManager
import com.example.interpreter.models.Id
import com.example.interpreter.models.PinManager
import com.example.interpreter.models.Program
import com.example.interpreter.models.ScopeBlock
import com.example.bluehits.R
import com.example.bluehits.ui.theme.*
import kotlin.reflect.KClass

class BlocksManager(private val context: Context) {
    private val _uiBlocks = mutableStateListOf<BlueBlock>()
    val uiBlocks: List<BlueBlock> get() = _uiBlocks
    private var _showTypeDialog = mutableStateOf(false)
    val showTypeDialog: State<Boolean> get() = _showTypeDialog
    private var _showValueNameDialog = mutableStateOf(false)
    val showValueNameDialog: State<Boolean> get() = _showValueNameDialog
    private var currentValueBlockType: String? = null

    private val _blockUpdated = mutableStateOf(false)
    val blockUpdated: State<Boolean> get() = _blockUpdated

    private var currentBlockType: String? = null
    private var onTypeSelected: ((DataType) -> Unit)? = null

    private var _showFunctionSelectionDialog = mutableStateOf(false)
    val showFunctionSelectionDialog: State<Boolean> get() = _showFunctionSelectionDialog
    private var currentFunctionSelectionType: String? = null

    private val _showCastFromDialog = mutableStateOf(false)
    val showCastFromDialog: State<Boolean> get() = _showCastFromDialog

    private val _showCastToDialog = mutableStateOf(false)
    val showCastToDialog: State<Boolean> get() = _showCastToDialog

    private var castFromType: KClass<*>? = null

    fun getAvailableFunctions(): List<String> {
        return _uiBlocks
            .filter { it.title.startsWith("def ") }
            .map { it.title.removePrefix("def ").trim() }
    }

    fun dismissFunctionSelectionDialog() {
        _showFunctionSelectionDialog.value = false
        currentFunctionSelectionType = null
    }

    private var screenWidthPx by mutableStateOf(0f)
    private var screenHeightPx by mutableStateOf(0f)

    fun updateScreenSize(width: Float, height: Float) {
        screenWidthPx = width
        screenHeightPx = height
    }

    fun updateBlock(blockId: Id, newBlock: BlueBlock) {
        val index = _uiBlocks.indexOfFirst { it.id == blockId }
        if (index != -1) {
            _uiBlocks[index].inputPins = newBlock.inputPins
            _uiBlocks[index].outputPins = newBlock.outputPins
            _blockUpdated.value = !_blockUpdated.value
        }
    }

    fun addNewBlock(type: String) {
        when (type) {
            getString(context, R.string.index_block_label), getString(context, R.string.append_block_label), getString(context, R.string.swap_block_label), getString(context, R.string.len_block_label), getString(context, R.string.array_block_label), getString(context, R.string.add_block_label),
            getString(context, R.string.sub_block_label), getString(context, R.string.multi_block_label), getString(context, R.string.div_block_label), getString(context, R.string.mod_block_label),  getString(context, R.string.greater_block_label),
            getString(context, R.string.less_block_label), getString(context, R.string.greaterOrEqual_block_label), getString(context, R.string.lessOrEqual_block_label), getString(context, R.string.equal_block_label)-> {
                currentBlockType = type
                _showTypeDialog.value = true
            }

            getString(context, R.string.function_def_block_label) -> showFunctionNameDialog(getString(context, R.string.function_def_block_label))
            getString(context, R.string.function_call_block_label) -> {
                currentFunctionSelectionType = getString(context, R.string.function_call_name_prefix)
                _showFunctionSelectionDialog.value = true
            }

            getString(context, R.string.function_return_block_label) -> {
                currentFunctionSelectionType = getString(context, R.string.function_return_name_prefix)
                _showFunctionSelectionDialog.value = true
            }

            getString(context, R.string.int_block_label) -> showFunctionNameDialog(getString(context, R.string.int_block_label))
            getString(context, R.string.float_block_label) -> showFunctionNameDialog(getString(context, R.string.float_block_label))
            getString(context, R.string.bool_block_label) -> showFunctionNameDialog(getString(context, R.string.bool_block_label))
            getString(context, R.string.string_block_label) -> showFunctionNameDialog(getString(context, R.string.string_block_label))
            getString(context, R.string.cast_block_label) -> {
                _showCastFromDialog.value = true
            }
            "Int" -> showFunctionNameDialog("Int")
            "Float" -> showFunctionNameDialog("Float")
            "Bool" -> showFunctionNameDialog("Bool")
            "String" -> showFunctionNameDialog("String")
            else -> createBlockWithoutType(type)
        }
    }

    fun onCastFromTypeSelected(type: DataType) {
        castFromType = when (type) {
            DataType.INT -> Int::class
            DataType.FLOAT -> Float::class
            DataType.DOUBLE -> Double::class
            DataType.LONG -> Long::class
        }
        _showCastFromDialog.value = false
        _showCastToDialog.value = true
    }

    fun onCastToTypeSelected(type: DataType) {
        _showCastToDialog.value = false

        val toType = when (type) {
            DataType.INT -> Int::class
            DataType.FLOAT -> Float::class
            DataType.DOUBLE -> Double::class
            DataType.LONG -> Long::class
        }

        castFromType?.let { fromType ->
            val logicBlock = BlockManager.createCastBlock(fromType, toType)
            val centerX = screenWidthPx
            val centerY = screenHeightPx
            _uiBlocks.add(BlockAdapter.wrapLogicBlock(logicBlock, centerX, centerY))
        }
        castFromType = null
    }

    fun dismissCastDialogs() {
        _showCastFromDialog.value = false
        _showCastToDialog.value = false
        castFromType = null
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
                getString(context, R.string.function_def_block_label) -> BlockManager.createFunctionDefinitionBlock(name)
                getString(context, R.string.function_call_block_label) -> BlockManager.createFunctionCalledBlock(name)
                getString(context, R.string.function_return_block_label) -> BlockManager.createFunctionReturnBlock(name)
                getString(context, R.string.int_block_label) -> BlockManager.createIntBlock(name)
                getString(context, R.string.float_block_label) -> BlockManager.createFloatBlock(name)
                getString(context, R.string.bool_block_label) -> BlockManager.createBoolBlock(name)
                getString(context, R.string.string_block_label) -> BlockManager.createStringBlock(name)
                "Array" -> {
                    when (currentValueBlockType) {
                        "Int" -> BlockManager.createArrayBlock(name, elementType = Int::class)
                        "Float" -> BlockManager.createArrayBlock(name, elementType = Float::class)
                        "Double" -> BlockManager.createArrayBlock(name, elementType = Double::class)
                        "Long" -> BlockManager.createArrayBlock(name, elementType = Long::class)
                        else -> throw IllegalStateException("No type selected for array")
                    }
                }
                else -> throw IllegalArgumentException("Unknown function dialog type")
            }

            val centerX = screenWidthPx
            val centerY = screenHeightPx
            _uiBlocks.add(BlockAdapter.wrapLogicBlock(logicBlock, centerX = centerX, centerY = centerY))
        }
    }

    fun onFunctionSelected(functionName: String) {
        _showFunctionSelectionDialog.value = false
        currentFunctionSelectionType?.let { type ->
            val logicBlock = when (type) {
                getString(context, R.string.function_call_name_prefix) -> BlockManager.createFunctionCalledBlock(functionName)
                getString(context, R.string.function_return_name_prefix) -> BlockManager.createFunctionReturnBlock(functionName)
                else -> throw IllegalArgumentException("Unknown function selection type: ${type}")
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
                getString(context, R.string.index_block_label) -> when (type) {
                    DataType.INT -> BlockManager.createIndexBlock<Int>()
                    DataType.FLOAT -> BlockManager.createIndexBlock<Float>()
                    DataType.DOUBLE -> BlockManager.createIndexBlock<Double>()
                    DataType.LONG -> BlockManager.createIndexBlock<Long>()
                }

                getString(context, R.string.append_block_label) -> when (type) {
                    DataType.INT -> BlockManager.createAppendBlock<Int>()
                    DataType.FLOAT -> BlockManager.createAppendBlock<Float>()
                    DataType.DOUBLE -> BlockManager.createAppendBlock<Double>()
                    DataType.LONG -> BlockManager.createAppendBlock<Long>()
                }

                getString(context, R.string.swap_block_label) -> when (type) {
                    DataType.INT -> BlockManager.createSwapBlock<Int>()
                    DataType.FLOAT -> BlockManager.createSwapBlock<Float>()
                    DataType.DOUBLE -> BlockManager.createSwapBlock<Double>()
                    DataType.LONG -> BlockManager.createSwapBlock<Long>()
                }

                getString(context, R.string.len_block_label) -> when (type) {
                    DataType.INT -> BlockManager.createLenBlock<Int>()
                    DataType.FLOAT -> BlockManager.createLenBlock<Float>()
                    DataType.DOUBLE -> BlockManager.createLenBlock<Double>()
                    DataType.LONG -> BlockManager.createLenBlock<Long>()
                }

                getString(context, R.string.array_block_label) -> when (type) {
                    DataType.INT -> BlockManager.createArrayBlock(elementType = Int::class)
                    DataType.FLOAT -> BlockManager.createArrayBlock(elementType = Float::class)
                    DataType.DOUBLE -> BlockManager.createArrayBlock(elementType = Double::class)
                    DataType.LONG -> BlockManager.createArrayBlock(elementType = Long::class)
                }

                getString(context, R.string.add_block_label) -> when (type) {
                    DataType.INT -> BlockManager.createAddBlock(type = Int::class)
                    DataType.FLOAT -> BlockManager.createAddBlock(type = Float::class)
                    DataType.DOUBLE -> BlockManager.createAddBlock(type = Double::class)
                    DataType.LONG -> BlockManager.createAddBlock(type = Long::class)
                }

                getString(context, R.string.sub_block_label) -> when (type) {
                    DataType.INT -> BlockManager.createSubBlock(type = Int::class)
                    DataType.FLOAT -> BlockManager.createSubBlock(type = Float::class)
                    DataType.DOUBLE -> BlockManager.createSubBlock(type = Double::class)
                    DataType.LONG -> BlockManager.createSubBlock(type = Long::class)
                }

                getString(context, R.string.multi_block_label) -> when (type) {
                    DataType.INT -> BlockManager.createMulBlock(type = Int::class)
                    DataType.FLOAT -> BlockManager.createMulBlock(type = Float::class)
                    DataType.DOUBLE -> BlockManager.createMulBlock(type = Double::class)
                    DataType.LONG -> BlockManager.createMulBlock(type = Long::class)
                }

                getString(context, R.string.div_block_label) -> when (type) {
                    DataType.INT -> BlockManager.createDivBlock(type = Int::class)
                    DataType.FLOAT -> BlockManager.createDivBlock(type = Float::class)
                    DataType.DOUBLE -> BlockManager.createDivBlock(type = Double::class)
                    DataType.LONG -> BlockManager.createDivBlock(type = Long::class)
                }

                getString(context, R.string.mod_block_label) -> when (type) {
                    DataType.INT -> BlockManager.createModBlock(type = Int::class)
                    DataType.FLOAT -> BlockManager.createModBlock(type = Float::class)
                    DataType.DOUBLE -> BlockManager.createModBlock(type = Double::class)
                    DataType.LONG -> BlockManager.createModBlock(type = Long::class)
                }

                getString(context, R.string.greater_block_label) -> when (type) {
                    DataType.INT -> BlockManager.createGreaterBlock(type = Int::class)
                    DataType.FLOAT -> BlockManager.createGreaterBlock(type = Float::class)
                    DataType.DOUBLE -> BlockManager.createGreaterBlock(type = Double::class)
                    DataType.LONG -> BlockManager.createGreaterBlock(type = Long::class)
                }

                getString(context, R.string.less_block_label) -> when (type) {
                    DataType.INT -> BlockManager.createLessBlock(type = Int::class)
                    DataType.FLOAT -> BlockManager.createLessBlock(type = Float::class)
                    DataType.DOUBLE -> BlockManager.createLessBlock(type = Double::class)
                    DataType.LONG -> BlockManager.createLessBlock(type = Long::class)
                }

                getString(context, R.string.greaterOrEqual_block_label) -> when (type) {
                    DataType.INT -> BlockManager.createGreaterOrEqualBlock(type = Int::class)
                    DataType.FLOAT -> BlockManager.createGreaterOrEqualBlock(type = Float::class)
                    DataType.DOUBLE -> BlockManager.createGreaterOrEqualBlock(type = Double::class)
                    DataType.LONG -> BlockManager.createGreaterOrEqualBlock(type = Long::class)
                }

                getString(context, R.string.lessOrEqual_block_label) -> when (type) {
                    DataType.INT -> BlockManager.createLessOrEqualBlock(type = Int::class)
                    DataType.FLOAT -> BlockManager.createLessOrEqualBlock(type = Float::class)
                    DataType.DOUBLE -> BlockManager.createLessOrEqualBlock(type = Double::class)
                    DataType.LONG -> BlockManager.createLessOrEqualBlock(type = Long::class)
                }

                getString(context, R.string.equal_block_label) -> when (type) {
                    DataType.INT -> BlockManager.createEqualBlock(type = Int::class)
                    DataType.FLOAT -> BlockManager.createEqualBlock(type = Float::class)
                    DataType.DOUBLE -> BlockManager.createEqualBlock(type = Double::class)
                    DataType.LONG -> BlockManager.createEqualBlock(type = Long::class)
                }

                else -> throw IllegalArgumentException(getString(context, R.string.unsupported_type))
            }
            val centerX = screenWidthPx
            val centerY = screenHeightPx
            _uiBlocks.add(BlockAdapter.wrapLogicBlock(logicBlock, centerX = centerX, centerY = centerY))
        }
    }

    private fun createBlockWithoutType(type: String) {
        val logicBlock = when (type) {
            getString(context, R.string.for_block_label) -> BlockManager.createForBlock()
            getString(context, R.string.print_block_label) -> BlockManager.createPrintBlock()
            getString(context, R.string.ifelse_block_label) -> BlockManager.createIfElseBlock()
            getString(context, R.string.math_block_label) -> BlockManager.createMathBlock()
            else -> throw IllegalArgumentException(getString(context, R.string.unsupported_type))
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
            initialX = 1f,
            initialY = 1f,
            color = BlockBodyColor,
            title = mainLogicBlock.name,
            inputPins = mainLogicBlock.inputs,
            outputPins = mainLogicBlock.outputs.subList(0, mainLogicBlock.outputs.size - 1),
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
        if (block.inBlockPin != null) {
            pinIds.add(block.inBlockPin.id)
        }
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

        val functionName = (block.logicBlock as? FunctionPartBlock)?.getFunctionName()
        functionName?.let {
            removeFunction(functionName)
        }

        _uiBlocks.remove(block)
    }

    private fun removeFunction(funcName: String) {
        val info = FunctionManager.getFunctionInfo(funcName)
        if (info == null) return
        // Важная фигня так как иначе может зайти в беск. рекурсию.
        FunctionManager.removeFunction(funcName)

        val idToDelete = mutableListOf<Id>()
        idToDelete.add(info.definitionBlock.id)
        info.callBlocks.forEach { block -> idToDelete.add(block.id) }
        info.returnBlocks.forEach { block -> idToDelete.add(block.id) }
        idToDelete.forEach { id ->
            val block = _uiBlocks.firstOrNull { it.id == id }
            block?.let {
                removeBlock(block, UIConnectionManager)
            }
        }
    }

    fun clearAllBlocks() {
        _uiBlocks.toList().forEach { block ->
            if (block.title != getString(context, R.string.main_block_title)) {
                removeBlock(block, UIConnectionManager)
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
                .background(DarkBackground, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Select data type",
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
                        containerColor = TypeDialogButtonContainerColor,
                        contentColor = TypeDialogButtonContentColor
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
    label: String? = "Function name",
    onNameEntered: (String) -> Unit,
    onDismiss: () -> Unit,
    onError: (String) -> Unit = {},
    context: Context
) {
    var functionName by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(DarkBackground, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = title,
                color = WhiteClassic,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = functionName,
                onValueChange = { functionName = it },
                label = { Text(label!!, color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = WhiteClassic),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        try {
                            if (functionName.isNotBlank()) {
                                onNameEntered(functionName)
                            }
                        } catch (e: Exception) {
                            onError(e.message?:getString(context, R.string.default_error))
                        }
                    }
                ),
                singleLine = true,
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
                    try {
                        if (functionName.isNotBlank()) {
                            onNameEntered(functionName)
                        }
                    } catch (e: Exception) {
                        onError(e.message?:getString(context, R.string.default_error))
                    }
                },
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WhiteClassic, contentColor = ContentColorFunctionName),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Create")
            }
        }
    }
}

@Composable
fun FunctionSelectionDialog(
    functions: List<String>,
    onFunctionSelected: (String) -> Unit,
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
                text = "Select function",
                style = MaterialTheme.typography.titleMedium,
                color = WhiteClassic,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            functions.forEach { function ->
                Button(
                    onClick = { onFunctionSelected(function) },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FunctionSelectionContainer,
                        contentColor = FunctionSelectionContent
                    )
                ) {
                    Text(text = function)
                }
            }
        }
    }
}

@Composable
fun CastTypeDialogs(
    blocksManager: BlocksManager
) {
    if (blocksManager.showCastFromDialog.value) {
        Dialog(onDismissRequest = { blocksManager.dismissCastDialogs() }) {
            Column(
                modifier = Modifier
                    .background(DarkBackground, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select source type",
                    style = MaterialTheme.typography.titleMedium,
                    color = WhiteClassic,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                DataType.values().forEach { type ->
                    Button(
                        onClick = { blocksManager.onCastFromTypeSelected(type) },
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TypeDialogButtonContainerColor,
                            contentColor = TypeDialogButtonContentColor
                        )
                    ) {
                        Text(text = type.title)
                    }
                }
            }
        }
    }

    if (blocksManager.showCastToDialog.value) {
        Dialog(onDismissRequest = { blocksManager.dismissCastDialogs() }) {
            Column(
                modifier = Modifier
                    .background(DarkBackground, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select target type",
                    style = MaterialTheme.typography.titleMedium,
                    color = WhiteClassic,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                DataType.values().forEach { type ->
                    Button(
                        onClick = { blocksManager.onCastToTypeSelected(type) },
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TypeDialogButtonContainerColor,
                            contentColor = TypeDialogButtonContentColor
                        )
                    ) {
                        Text(text = type.title)
                    }
                }
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