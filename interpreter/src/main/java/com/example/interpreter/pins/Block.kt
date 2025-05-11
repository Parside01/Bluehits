package com.example.interpreter.pins

import com.example.interpreter.models.Id
import com.example.interpreter.models.Pin
import com.example.interpreter.models.PinType

// По задумке этот пин связывает выполнение двух блоков
// Ведь они необязательно должны быть связаны переменными,
// Мы просто можем захотеть выполнить два блока последовательно.
class PinBlockId (
    id: Id,
    ownId: Id,
    name: String,
): Pin(id, ownId, name, PinType.BLOCK) {
    // По факту - заглушка.
    private var block: Id = Id("pin-block-")
    override var zeroValue: Any = Id("pin-block-")

    init {
        isSet = block != zeroValue
    }

    override fun getValue(): Any {
        return id
    }

    override fun setValue(value: Any) {
        when (value) {
            is Id -> {
                this.block = value
                this.isSet = true
            }
            is String -> {
                this.block = Id(value)
                this.isSet = true
            }
            else -> {
                throw IllegalArgumentException("Value must be Boolean")
            }
        }
    }
}