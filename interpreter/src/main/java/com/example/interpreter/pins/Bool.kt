package com.example.interpreter.pins

import com.example.interpreter.models.Id
import com.example.interpreter.models.Pin
import com.example.interpreter.models.PinType

internal class PinBool internal constructor(
    id: Id,
    ownId: Id,
    name: String,
    private var value: Boolean = false
) : Pin(id, ownId, name, PinType.BOOL) {
    override var zeroValue: Any = false

    init {
        isSet = value != zeroValue
    }

    override fun getValue(): Any {
        if (!isSet) return zeroValue
        return value
    }

    override fun setValue(value: Any) {
        if (value is Boolean) {
            this.value = value
            this.isSet = true
        } else {
            throw IllegalArgumentException("Value must be Boolean")
        }
    }
}