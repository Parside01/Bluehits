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

    fun handlePinClick(pin: PinUi) {
        if (pin == tempPin) {
            tempPin = null
            return
        }

        if (tempPin == null) {
            tempPin = pin
            return
        }

        // TODO: Спросить как сделать лучше.
        if (pin.type == InOutPinType.INPUT && tempPin!!.type == InOutPinType.INPUT) {
            tempPin = pin
        } else if (pin.type == InOutPinType.INPUT && tempPin!!.type == InOutPinType.OUTPUT) {
            val connectionId = connect(tempPin!!.id, pin.id)
            connections.add(Pair(pin, tempPin!!))
            tempPin = null
        } else if (pin.type == InOutPinType.OUTPUT && tempPin!!.type == InOutPinType.INPUT) {
            val connectionId = connect(pin.id, tempPin!!.id)
            connections.add(Pair(tempPin!!, pin))
            tempPin = null
        } else {
            tempPin = pin
        }
    }

    private fun connect(fromId: Id, toId: Id): Id? {
        try {
            val fromPin = PinManager.getPin(fromId)
            val toPin = PinManager.getPin(toId)

            var connectionId: Id? = null
            fromPin?.let { from ->
                toPin?.let { to ->
                    connectionId = ConnectionManager.connect(from, to).id
                }
            }
            return connectionId
        } catch (e: Exception) {
            print(e)
        }
        return null
    }
}
