package pins

import model.Pin
import model.PinType
import kotlin.Int

class PinBool(
    id: String,
    name: String,
    private var value: Boolean
) : Pin(id, name, PinType.BOOL) {

    override fun getValue(): Any {
        return value
    }

    override fun setValue(value: Any) {
        this.value = value as Boolean
    }
}