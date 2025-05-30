package com.example.interpreter.blocks

import com.example.interpreter.models.Block
import com.example.interpreter.models.ExecutionState
import com.example.interpreter.models.Id
import com.example.interpreter.models.PinManager
import kotlin.reflect.KClass

class CastBlock <F: Any, T: Any>(
    id: Id,
    fromType: KClass<F>,
    toType: KClass<T>,
) : Block(
    id,
    "Cast",
    mutableListOf(PinManager.createPin("from", ownId = id, type = fromType)),
    mutableListOf(PinManager.createPin("to", ownId = id, type = toType)),
) {
    override fun execute(): ExecutionState {
        val value = inputs.first().getValue() as F
        outputs[0].setValue(value as T)
        return ExecutionState.COMPLETED
    }
}