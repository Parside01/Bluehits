package com.example.interpreter.blocks

import com.example.interpreter.models.Block
import com.example.interpreter.models.ExecutionState
import com.example.interpreter.models.Id
import com.example.interpreter.models.PinManager
import java.util.Arrays
import kotlin.reflect.KClass

class IndexBlock<T: Any> (
    id: Id,
    type: KClass<T>,
) : Block(id,
    "Index",
    mutableListOf(PinManager.createPinArray("arr", ownId = id, elementType = type), PinManager.createPinInt("index", ownId = id, value = 0)),
    mutableListOf(PinManager.createPin("value", ownId = id, type = type))
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

class SwapBlock<T:Any> (
    id: Id,
    type: KClass<T>
) : Block(id,
    "Swap",
    mutableListOf(
        PinManager.createPinArray("arr", ownId = id, elementType = type),
        PinManager.createPinInt("i", ownId = id, value = 0),
        PinManager.createPinInt("j", ownId = id, value = 0)),
    mutableListOf(PinManager.createPinArray<T>("new", ownId = id, elementType = type))
) {
    override fun execute(): ExecutionState {
        val array = inputs.first().getValue() as List<*>
        val i = inputs[1].getValue() as Int
        val j = inputs[2].getValue() as Int

        if (i < 0 || i >= array.size || j < 0 || j >= array.size) {
            throw IndexOutOfBoundsException("Index $i or $j is out of bounds for array of size ${array.size}")
        }

        val newArray = ArrayList(array)

        val temp = newArray[i]
        newArray[i] = newArray[j]
        newArray[j] = temp

        outputs[0].setValue(newArray)
        return ExecutionState.COMPLETED
    }
}