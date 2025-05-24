package com.example.bluehits.ui

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.interpreter.models.BlockManager
import com.example.interpreter.models.ConnectionManager
import com.example.interpreter.models.Id
import com.example.interpreter.models.PinManager
import com.example.interpreter.models.Program

class BlocksManager {
    private val _uiBlocks = mutableStateListOf<BlueBlock>()
    val uiBlocks: List<BlueBlock> get() = _uiBlocks

    fun getPrintBlockValue(uiBlocks: List<BlueBlock>): Any? {
        uiBlocks.forEach { block ->
            if (block.title == "Print") {
                val logicBlock = BlockManager.getBlock(block.id)
                return logicBlock?.let { notNullBlock -> notNullBlock.inputs[0].getValue() }
            }
        }
        return null
    }

    fun addNewBlock(type: String, onError: (String) -> Unit = {}) {
        if (type == "Main" && _uiBlocks.any { it.title == "Main" }) {
            onError("Блок Main уже существует и не может быть добавлен повторно")
            return
        }
        val logicBlock = when (type) {
            "Main" -> Program.getMainBlock()
            "Index" -> BlockManager.createIndexBlock()
            "Append" -> BlockManager.createAppendBlock()
            "Array" -> BlockManager.createArrayBlock()
            "For" -> BlockManager.createForBlock()
            "Int" -> BlockManager.createIntBlock()
            "Add" -> BlockManager.createAddBlock(type = Int::class)
            "Bool" -> BlockManager.createBoolBlock()
            "Float" -> BlockManager.createFloatBlock()
            "Print" -> BlockManager.createPrintBlock()
            "Sub" -> BlockManager.createSubBlock(type = Int::class)
            "IfElse" -> BlockManager.createIfElseBlock()
            else -> throw IllegalArgumentException("Unsupported type")
        }
        _uiBlocks.add(BlockAdapter.wrapLogicBlock(logicBlock))
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
        }
        _uiBlocks.clear()
    }
}