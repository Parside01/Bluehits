package com.example.bluehits.ui.editPanel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.bluehits.ui.BlueBlock
import com.example.interpreter.models.Pin

object BlockEditManager {
    var editState by mutableStateOf<BlockEditState?>(null)
        private set

    fun shouldShowEditPanel(block: BlueBlock): Boolean {
        // Разрешаем показ панели для блока Function def или если есть входные пины
        return block.title == "definition" || block.inputPins.isNotEmpty()
    }

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
        editState = null
    }
}