package com.example.interpreter.blocks

import com.example.interpreter.models.Block
import com.example.interpreter.models.ExecutionState
import com.example.interpreter.models.Id
import com.example.interpreter.models.PinManager
import kotlin.reflect.KClass


class BinaryOperatorBlock<T: Any>(
    id: Id,
    name: String,
    private val operation: (T, T) -> T,
    private val type: KClass<T>,
) : Block(
    id,
    name,
    mutableListOf(
        PinManager.createPin("a", ownId = id, type = type),
        PinManager.createPin("b", ownId = id, type = type),),
    mutableListOf(PinManager.createPin("c", ownId = id, type = type)),
) {
    override fun execute(): ExecutionState {
        try {
            val result = operation(inputs.first().getValue() as T, inputs.last().getValue() as T)
            outputs.single().setValue(result)
        } catch (e: Exception) {
            throw e
        }
        return ExecutionState.COMPLETED
    }
}

class BinaryLogicOperatorBlock<T: Any>(
    id: Id,
    name: String,
    private val operation: (T, T) -> Boolean,
    private val type: KClass<T>,
) : Block(
    id,
    name,
    mutableListOf(
        PinManager.createPin("a", ownId = id, type = type),
        PinManager.createPin("b", ownId = id, type = type),),
    mutableListOf(PinManager.createPinBool("c", ownId = id)),
) {
    override fun execute(): ExecutionState {
        try {
            val result = operation(inputs.first().getValue() as T, inputs.last().getValue() as T)
            outputs.single().setValue(result)
        } catch (e: Exception) {
            throw e
        }
        return ExecutionState.COMPLETED
    }
}