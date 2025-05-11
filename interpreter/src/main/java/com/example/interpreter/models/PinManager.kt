package com.example.interpreter.models

import com.example.interpreter.pins.PinAny
import com.example.interpreter.pins.PinBlockId
import com.example.interpreter.pins.PinBool
import com.example.interpreter.pins.PinInt
import java.util.concurrent.atomic.AtomicInteger

object PinManager {
    private val pinRegistry = mutableMapOf<String, Pin>()
    private val idCounter = AtomicInteger(0)

    private fun generateId(): Id {
        return Id("pin-${idCounter.getAndIncrement()}")
    }

    fun getPin(id: String) = pinRegistry[id]

    private fun <T : Pin> createPin(createPinFunc: (Id) -> T): T {
        val id = generateId()
        val pin = createPinFunc(id)
        pinRegistry[id.string()] = pin
        return pin
    }

    fun setPinValue(id: Id, value: Any) {
        getPin(id.toString())?.setValue(value)
    }

//    fun createPinBlock(name: String, block: Id = Id("pin-block-"), ownId: Id = Id("null")): Pin {
//        return createPin { id -> PinBlockId(id, ownId, name, block) }
//    }

    fun createPinBlock(name: String, ownId: Id = Id("null")): Pin {
        return createPin { id -> PinBlockId(id, ownId, name) }
    }

    fun createPinInt(name: String, value: Int = 0, ownId: Id = Id("null")): Pin {
        return createPin { id -> PinInt(id, ownId,  name, value) }
    }

    fun createPinAny(name: String, value: Any = "null", ownId: Id = Id("null")): Pin {
        return createPin { id -> PinAny(id, ownId, name, value) }
    }

    fun createPinBool(name: String, value: Boolean = false, ownId: Id = Id("null")): Pin {
        return createPin { id -> PinBool(id, ownId, name, value) }
    }
}