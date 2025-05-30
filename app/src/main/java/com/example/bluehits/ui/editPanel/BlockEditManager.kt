package com.example.bluehits.ui.editPanel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.bluehits.ui.BlueBlock
import com.example.interpreter.blocks.FunctionDefinitionBlock
import com.example.interpreter.models.Pin

object BlockEditManager {
    var editState by mutableStateOf<BlockEditState?>(null)
        private set

    fun shouldShowEditPanel(block: BlueBlock): Boolean {
        return block.title.startsWith("def ") ||
                block.title.startsWith("return ") ||
                block.inputPins.isNotEmpty() ||
                block.outputPins.isNotEmpty()    }

    fun showEditPanel(block: BlueBlock) {
        if (!shouldShowEditPanel(block)) return

        val pinFields = mutableListOf<PinEditField>().apply {
            addAll(block.inputPins.map { pin ->
                PinEditField(
                    pin = pin,
                    isInput = true,
                    value = pin.getStringValue()
                )
            })
            addAll(block.outputPins.map { pin ->
                PinEditField(
                    pin = pin,
                    isInput = false,
                    value = pin.getStringValue()
                )
            })
        }

        editState = BlockEditState(
            blockId = block.id,
            pinFields = pinFields,
            isVisible = true
        )
    }

    fun updatePinValue(pin: Pin, newValue: Any) {
        pin.setValue(newValue)
        editState = editState?.copy(
            pinFields = editState!!.pinFields.map { field ->
                if (field.pin == pin) {
                    field.withNewValue(newValue)
                } else {
                    field
                }
            }
        )
    }

    fun hideEditPanel() {
        editState = editState?.copy(isVisible = false)
    }
}