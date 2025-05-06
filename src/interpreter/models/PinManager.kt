package interpreter.models

import interpreter.pins.PinAny
import interpreter.pins.PinBlockId
import interpreter.pins.PinBool
import interpreter.pins.PinInt
import java.util.concurrent.atomic.AtomicInteger

object PinManager {
    private val pinRegistry = mutableMapOf<String, Pin>()
    private val idCounter = AtomicInteger(0)

    private fun generateId(): Id {
        return Id("pin-${idCounter.getAndIncrement()}")
    }

    fun getPin(id: String) = pinRegistry[id]

    // Лучше пока не использовать.
    fun createPin(name: String, type: PinType, value: Any): Pin? {
        if (type == PinType.BOOL && value is Boolean) {
            return createPinBool(name, value)
        } else if (type == PinType.INT && value is Int) {
            return createPinInt(name, value)
        }
        return createPinAny(name, value)
    }

    private fun <T : Pin> createPin(createPinFunc: (Id) -> T): T {
        val id = generateId()
        val pin = createPinFunc(id)
        pinRegistry[id.string()] = pin
        return pin
    }

    fun createPinBlock(name: String, block: Id = Id("pin-block-"), ownId: Id = Id("null")): Pin {
        return createPin { id -> PinBlockId(id, ownId, name, block) }
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