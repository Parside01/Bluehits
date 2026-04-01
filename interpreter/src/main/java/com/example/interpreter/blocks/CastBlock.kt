package com.example.interpreter.blocks

import com.example.interpreter.models.Block
import com.example.interpreter.models.ExecutionState
import com.example.interpreter.models.Id
import com.example.interpreter.models.PinManager
import kotlin.math.roundToInt
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

    private val converters: Map<Pair<KClass<*>, KClass<*>>, (Any) -> Any?> = mapOf(
        Pair(Float::class, Int::class) to { (it as Float).roundToInt() },
        Pair(Double::class, Int::class) to { (it as Double).roundToInt() },
        Pair(Int::class, Float::class) to { (it as Int).toFloat() },
        Pair(Double::class, Float::class) to { (it as Double).toFloat() },
        Pair(Float::class, Double::class) to { (it as Float).toDouble() },
        Pair(Int::class, Double::class) to { (it as Int).toDouble() },
        Pair(Any::class, String::class) to { it.toString() },
        Pair(String::class, Int::class) to { (it as String).toIntOrNull() },
        Pair(String::class, Float::class) to { (it as String).toFloatOrNull() },
        Pair(String::class, Double::class) to { (it as String).toDoubleOrNull() },
    )

    override fun execute(): ExecutionState {
        val rawValue: Any? = inputs.first().getValue()
        if (rawValue == null) return ExecutionState.COMPLETED

        val converter = converters[Pair(fromType, toType)]

        val valueAsT: T = if (converter != null) {
            converter(rawValue) as? T
                ?: throw IllegalArgumentException("Failed to cast from ${fromType.simpleName} to ${toType.simpleName}")
        } else if (toType.isInstance(rawValue)) {
            toType.cast(rawValue)
        } else {
            throw IllegalArgumentException("Unsupported cast from ${fromType.simpleName} to ${toType.simpleName}\"")
        }

        outputs[0].setValue(valueAsT)
        return ExecutionState.COMPLETED
    }
}