package interpreter.pins

import interpreter.models.Id
import interpreter.models.Pin
import interpreter.models.PinType

class PinAny internal constructor(
    id: Id,
    name: String,
    private var value: Any = "null"
) : Pin(id, name, PinType.ANY) {
    override var zeroValue: Any = "null"

    init {
        isSet = value != zeroValue
    }

    override fun getValue(): Any {
        if (!isSet) return zeroValue
        return value
    }

    override fun setValue(value: Any) {
        this.value = value
        this.isSet = true
    }
}