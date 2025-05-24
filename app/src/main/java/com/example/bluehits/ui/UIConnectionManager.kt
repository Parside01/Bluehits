package com.example.bluehits.ui

import androidx.compose.runtime.mutableStateListOf
import com.example.interpreter.models.Connection
import com.example.interpreter.models.ConnectionManager
import com.example.interpreter.models.Id
import com.example.interpreter.models.Pin
import com.example.interpreter.models.PinManager

class UIConnectionManager {
    private var tempPin: PinUi? = null
    val connections = mutableStateListOf<Pair<PinUi, PinUi>>()

    fun handlePinClick(pin: PinUi, onError: (String) -> Unit) {
        if (pin == tempPin) {
            tempPin = null
            return
        }

        if (tempPin == null) {
            tempPin = pin
            return
        }

        if (pin.type == tempPin!!.type) {
            onError("Нельзя соединить пины одного типа: оба ${pin.type}")
            tempPin = null
            return
        }

        try {
            if (pin.type == InOutPinType.INPUT && tempPin!!.type == InOutPinType.OUTPUT) {
                val connectionId = connect(tempPin!!.id, pin.id)
                if (connectionId != null) {
                    connections.add(Pair(pin, tempPin!!))
                } else {
                    onError("Не удалось создать соединение между пинами")
                }
                tempPin = null
            } else if (pin.type == InOutPinType.OUTPUT && tempPin!!.type == InOutPinType.INPUT) {
                val connectionId = connect(pin.id, tempPin!!.id)
                if (connectionId != null) {
                    connections.add(Pair(tempPin!!, pin))
                } else {
                    onError("Не удалось создать соединение между пинами")
                }
                tempPin = null
            } else {
                tempPin = pin
            }
        } catch (e: Exception) {
            onError("Ошибка при соединении пинов: ${e.message}")
            tempPin = null
        }
    }

    private fun connect(fromId: Id, toId: Id): Id? {
        val fromPin = PinManager.getPin(fromId)
        val toPin = PinManager.getPin(toId)

        if (fromPin == null || toPin == null) {
            return null
        }

        val connection = ConnectionManager.connect(fromPin, toPin)
        return connection?.id
    }
}