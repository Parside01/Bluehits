package interpreter.models

import interpreter.pins.PinAny
import interpreter.pins.PinBool
import interpreter.pins.PinInt
import java.util.concurrent.atomic.AtomicInteger

object PinManager {
    private val pinRegistry = mutableMapOf<String, Pin>()
    private val idCounter = AtomicInteger(0)

    private fun generateId(): String {
        return "pin-${idCounter.getAndIncrement()}"
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

    fun createPinInt(name: String, value: Int = 0): Pin {
        val id = generateId()
        val pin = PinInt(id, name, value);
        pinRegistry[id] = pin
        return pin
    }

    fun createPinAny(name : String, value : Any = "null") : Pin {
        val id = generateId()
        val pin = PinAny(id, name, value)
        pinRegistry[id] = pin
        return pin
    }

    fun createPinBool(name: String, value: Boolean = false): Pin {
        val id = generateId()
        val pin = PinBool(id, name, value);
        pinRegistry[id] = pin
        return pin
    }
}