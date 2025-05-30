package com.example.bluehits.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.interpreter.models.Connection
import com.example.interpreter.models.ConnectionManager
import com.example.interpreter.models.Id
import com.example.interpreter.models.Pin
import com.example.interpreter.models.PinManager

class UIConnectionManager {
    var tempPin = mutableStateOf<PinUi?>(null)
    val connections = mutableStateListOf<Pair<PinUi, PinUi>>()

    fun handlePinClick(pin: PinUi, onError: (String) -> Unit = {}) {
        if (pin == tempPin.value) {
            tempPin.value = null
            return
        }

        if (tempPin.value == null) {
            tempPin.value = pin
            return
        }

        if (pin.type == tempPin!!.value!!.type) {
            onError("Нельзя соединить пины одного типа: оба ${pin.type}")
            tempPin.value = pin
            return
        }

        try {
            if (pin.type == InOutPinType.INPUT && tempPin!!.value!!.type == InOutPinType.OUTPUT) {
                val connectionId = connect(tempPin!!.value!!.id, pin.id)
                if (connectionId != null) {
                    connections.add(Pair(pin, tempPin!!.value!!))
                } else {
                    onError("Не удалось создать соединение между пинами")
                }
                tempPin.value = null
            } else if (pin.type == InOutPinType.OUTPUT && tempPin!!.value!!.type == InOutPinType.INPUT) {
                val connectionId = connect(pin.id, tempPin!!.value!!.id)
                if (connectionId != null) {
                    connections.add(Pair(tempPin!!.value!!, pin))
                } else {
                    onError("Не удалось создать соединение между пинами")
                }
                tempPin.value = null
            } else {
                tempPin.value = pin
            }
        } catch (e: Exception) {
            onError("Ошибка при соединении пинов: ${e.message}")
            tempPin.value = null
        }
    }
    fun getSelectedPinId(): Id? = tempPin?.value!!.id

    private fun connect(fromId: Id, toId: Id): Id? {
        val fromPin = PinManager.getPin(fromId)
        val toPin = PinManager.getPin(toId)

        if (fromPin == null || toPin == null) {
            return null
        }

        val connection = ConnectionManager.connect(fromPin, toPin)
        return connection.id
    }
}