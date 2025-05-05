package interpreter.blocks

import interpreter.models.Block
import interpreter.models.Id
import interpreter.models.PinManager

class BinaryOperatorBlock (
    id: Id,
    name: String,
    private val operation: (Any, Any) -> Any
) : Block(
    id,
    name,
    mutableListOf(PinManager.createPinAny("a"), PinManager.createPinAny("b")),
    mutableListOf(PinManager.createPinAny("c")),
) {
    override fun execute() {
        try {
            val result = operation(inputs.first().getValue(), inputs.last().getValue())
            outputs.single().setValue(result)
        } catch (e: Exception) {
            throw e
        }
    }
}