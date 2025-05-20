package com.example.bluehits.ui.blockEditPanel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.bluehits.ui.BlocksManager
import com.example.bluehits.ui.BlueBlock
import com.example.interpreter.models.Id
import com.example.interpreter.models.Pin

object BlockEditManager {
    var editState by mutableStateOf<BlockEditState?>(null)
        private set

    fun shouldShowEditPanel(block: BlueBlock): Boolean {
        return block.inputPins.isNotEmpty()
    }

    fun showEditPanel(block: BlueBlock) {
        if (!shouldShowEditPanel(block)) return

        val pinFields = mutableListOf<PinEditField>().apply {
            addAll(block.inputPins.map { pin ->
                PinEditField(
                    pin = pin,
                    isInput = true,
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
        println("${ pin.name }, ${ newValue }")
        println(pin.getValue())
        editState = editState?.copy()
    }

    fun hideEditPanel() {
        editState = null
    }
}