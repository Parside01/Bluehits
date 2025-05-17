package com.example.bluehits.ui

import androidx.compose.runtime.mutableStateListOf

class ConnectionsManager {
    private var tempPin: PinUi? = null
    val connections = mutableStateListOf<Pair<PinUi, PinUi>>()

    fun handlePinClick(pin: PinUi) {
        if (tempPin == null) {
            tempPin = pin
        }
        else {
            if (pin == tempPin) {
                tempPin = null
            }
            else {
                if (pin.type != tempPin!!.type) {
                    connections.add(Pair(tempPin!!, pin))
                    tempPin = null
                }
                else {
                    tempPin = pin
                }
            }
        }
    }
}