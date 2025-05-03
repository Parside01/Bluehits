package interpreter.pins

import interpreter.models.Id
import interpreter.models.Pin
import interpreter.models.PinType

class PinInt internal constructor(
    id: Id,
    name: String,
    private var value: Int = 0
) : Pin(id, name, PinType.INT) {
    override var zeroValue: Any = 0

    init {
        isSet = value != zeroValue
    }

    override fun getValue(): Any {
        if (!isSet) return zeroValue
        return value
    }

    override fun setValue(value: Any) {
        if (value is Int) {
            this.value = value
            this.isSet = true
        } else {
            throw IllegalArgumentException("Value must be Int")
        }
    }
}