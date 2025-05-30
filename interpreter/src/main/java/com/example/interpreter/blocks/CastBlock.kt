package com.example.interpreter.blocks

import com.example.interpreter.models.Block
import com.example.interpreter.models.ExecutionState
import com.example.interpreter.models.Id
import com.example.interpreter.models.PinManager
import kotlin.reflect.KClass
import kotlin.reflect.cast

class CastBlock<F : Any, T : Any>(
    id: Id,
    val fromType: KClass<F>,
    val toType: KClass<T>,
) : Block(
    id,
    "Cast",
    mutableListOf(PinManager.createPin("from", ownId = id, type = fromType)),
    mutableListOf(PinManager.createPin("to", ownId = id, type = toType)),
) {
    override fun execute(): ExecutionState {
        val rawValue: Any? = inputs.first().getValue()

        val valueAsF: F = fromType.cast(rawValue)
        val valueAsT: T = toType.cast(valueAsF)

        outputs[0].setValue(valueAsT)
        return ExecutionState.COMPLETED
    }
}