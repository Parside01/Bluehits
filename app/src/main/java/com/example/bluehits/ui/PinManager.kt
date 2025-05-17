package com.example.bluehits.ui

import androidx.compose.ui.geometry.Offset

object PinManager {
    private val pins = mutableListOf<PinUi>()
    private const val PIN_RADIUS = 10f

    fun addPin(pin: PinUi) = pins.add(pin)

    fun findPinAt(position: Offset): PinUi? {
        return pins.firstOrNull { pin ->
            val center = Offset(
                pin.parentBlock.x + pin.ownOffset.x,
                pin.parentBlock.y + pin.ownOffset.y
            )
            position.minus(center).getDistance() <= PIN_RADIUS * 10
        }
    }

    fun clear() = pins.clear()
}