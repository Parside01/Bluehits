package interpreter.blocks

import com.example.interpreter.models.Block
import com.example.interpreter.models.ExecutionState
import interpreter.models.Id
import interpreter.models.PinManager

class IfElseBlock(
    id: Id,
) : Block(
    id,
    "IfElse",
    mutableListOf(PinManager.createPinBool("a", ownId = id)),
    mutableListOf(PinManager.createPinBlock("if", ownId = id), PinManager.createPinBlock("else", ownId = id)),
) {
    override fun execute(): ExecutionState {
        if (inputs.single().getValue() == true) {
            outputs[1] = outputs[0]
        } else {
            outputs[0] = outputs[1]
        }
        return ExecutionState.COMPLETED
    }
}