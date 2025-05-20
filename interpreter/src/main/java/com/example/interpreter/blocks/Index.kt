package com.example.interpreter.blocks

import com.example.interpreter.models.Block
import com.example.interpreter.models.ExecutionState
import com.example.interpreter.models.Id
import com.example.interpreter.models.PinManager

class IndexBlock (
    id: Id
) : Block(id,
    "Index",
    mutableListOf(PinManager.createPinArray("arr", ownId = id), PinManager.createPinInt("index", ownId = id, value = 0)),
    mutableListOf(PinManager.createPinAny("value", ownId = id))
) {
    override fun execute(): ExecutionState {
        val arrPin = pinByName("arr")?.getValue()
        val indexPin = pinByName("index")?.let { pin -> pin.getValue() as? Int }

        when {
            arrPin is List<*> && indexPin != null -> {
                if (indexPin < 0 || indexPin >= arrPin.size) {
                    throw IndexOutOfBoundsException("Index $indexPin is out of bounds for array of size ${arrPin.size}")
                }
                val valuePin = pinByName("value")
                valuePin?.setValue(arrPin[indexPin])
            }
            arrPin !is List<*> -> throw IllegalArgumentException("Value in ${pinByName("arr")?.name} is not a list")
            indexPin == null -> throw IllegalArgumentException("Index pin value is null")
        }
        return ExecutionState.COMPLETED
    }
}