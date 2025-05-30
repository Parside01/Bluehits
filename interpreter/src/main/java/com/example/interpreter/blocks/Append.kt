package com.example.interpreter.blocks

import com.example.interpreter.models.Block
import com.example.interpreter.models.ExecutionState
import com.example.interpreter.models.Id
import com.example.interpreter.models.PinManager
import kotlin.reflect.KClass

class AppendBlock <T: Any>(
    id: Id,
    type: KClass<T>
) : Block(
    id,
    "Append",
    mutableListOf(PinManager.createPinArray("arr", ownId = id, elementType = type), PinManager.createPinAny("value", ownId = id)),
    mutableListOf(PinManager.createPinArray("new", ownId = id, elementType = type)),
) {
    override fun execute(): ExecutionState {
        val array = inputs.first().getValue() as List<*>
        val value = inputs.last().getValue()
        val newArray = array + value
        outputs[0].setValue(newArray)
        return ExecutionState.COMPLETED
    }
}