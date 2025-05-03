package interpreter.pins

import interpreter.models.Id
import interpreter.models.Pin
import interpreter.models.PinType

// По задумке этот пин связывает выполнение двух блоков
// Ведь они необязательно должны быть связаны переменными,
// Мы просто можем захотеть выполнить два блока последовательно.
class PinBlockId (
    id: Id,
    name: String,
    var block: Id = Id("block-")
): Pin(id, name, PinType.BLOCK) {
    override var zeroValue: Any = Id("block-")

    override fun getValue(): Any {
        if (!isSet) return zeroValue
        return block
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