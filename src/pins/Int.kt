package pins

import model.Pin
import model.PinType

class PinInt(
    id: String,
    name: String,
    private var value: Int
) : Pin(id, name, PinType.INT) {

    override fun getValue(): Any {
        return value
    }

    override fun setValue(value: Any) {
        this.value = value as Int
    }
}