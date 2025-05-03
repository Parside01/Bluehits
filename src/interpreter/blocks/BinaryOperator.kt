package interpreter.blocks

import interpreter.models.Block
import interpreter.models.BlockId
import interpreter.models.PinManager

class BinaryOperatorBlock (
    id: BlockId,
    name: String,
    private val operation: (Any, Any) -> Any
) : Block(
    id,
    name,
    listOf(PinManager.createPinAny("a"), PinManager.createPinAny("b")),
    listOf(PinManager.createPinAny("c")),
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