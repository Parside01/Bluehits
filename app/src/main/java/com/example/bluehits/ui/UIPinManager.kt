package com.example.bluehits.ui

import androidx.compose.ui.geometry.Offset
import com.example.interpreter.models.Id

object UIPinManager {
    private val pins = mutableMapOf<Id, PinUi>()
    private const val PIN_RADIUS = 15f

    fun addPin(newPin: PinUi) {
        pins[newPin.id] = newPin

        updateConnectionsWithPin(newPin)

        if (UIConnectionManager.tempPin.value?.id == newPin.id) {
            UIConnectionManager.tempPin.value = newPin
        }
    }

    fun findPinAt(position: Offset): PinUi? {
        return pins.values.firstOrNull { pin ->
            val center = Offset(
                pin.parentBlock.x + pin.ownOffset.x,
                pin.parentBlock.y + pin.ownOffset.y
            )
            position.minus(center).getDistance() <= PIN_RADIUS * 2
        }
    }

    fun clearPinsForBlock(block: BlueBlock) {
        val pinsToRemoveIds = pins.filterValues { it.parentBlock == block }.keys.toList()

        pinsToRemoveIds.forEach { pinIdToRemove ->
            val updatedConnections = UIConnectionManager.connections.filter { (pin1, pin2) ->
                pin1.id != pinIdToRemove && pin2.id != pinIdToRemove
            }
            UIConnectionManager.connections.clear()
            UIConnectionManager.connections.addAll(updatedConnections)

            if (UIConnectionManager.tempPin.value?.id == pinIdToRemove) {
                UIConnectionManager.tempPin.value = null
            }

            pins.remove(pinIdToRemove)
        }
    }

    private fun updateConnectionsWithPin(newPin: PinUi) {
        val updatedConnections = UIConnectionManager.connections.map { (pin1, pin2) ->
            var updatedPin1 = pin1
            var updatedPin2 = pin2
            if (pin1.id == newPin.id) {
                updatedPin1 = newPin
            }
            if (pin2.id == newPin.id) {
                updatedPin2 = newPin
            }
            updatedPin1 to updatedPin2
        }
        UIConnectionManager.connections.clear()
        UIConnectionManager.connections.addAll(updatedConnections)
    }

    fun clear() = pins.clear()
}