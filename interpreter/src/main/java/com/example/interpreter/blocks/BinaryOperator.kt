package interpreter.blocks

import com.example.interpreter.models.Block
import com.example.interpreter.models.ExecutionState
import interpreter.models.Id
import interpreter.models.PinManager

class BinaryOperatorBlock (
    id: Id,
    name: String,
    private val operation: (Any, Any) -> Any
) : Block(
    id,
    name,
    mutableListOf(PinManager.createPinAny("a", ownId = id), PinManager.createPinAny("b", ownId = id)),
    mutableListOf(PinManager.createPinAny("c", ownId = id)),
) {
    override fun execute(): ExecutionState {
        try {
            val result = operation(inputs.first().getValue(), inputs.last().getValue())
            outputs.single().setValue(result)
        } catch (e: Exception) {
            throw e
        }
        return ExecutionState.COMPLETED
    }
}