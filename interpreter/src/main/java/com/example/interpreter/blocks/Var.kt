package interpreter.blocks

import com.example.interpreter.models.Block
import com.example.interpreter.models.ExecutionState
import interpreter.models.Id
import interpreter.models.PinManager

class IntBlock internal constructor(
    id: Id,
    default: Int = 0
) : Block(
    id,
    "Int",
    mutableListOf(PinManager.createPinInt("a", default, id)),
    mutableListOf(PinManager.createPinInt("b", default, id))
) {
    override fun execute(): ExecutionState {
        if (inputs.size != outputs.size) {
            throw Exception("Number of outputs must be equal")
        }

        for (i in inputs.indices) {
            outputs[i].setValue(inputs[i].getValue())
        }
        return ExecutionState.COMPLETED
    }
}

class BoolBlock internal constructor(
    id: Id,
    default: Boolean = false
) : Block(
    id,
    "Bool",
    mutableListOf(PinManager.createPinBool("a", default)),
    mutableListOf(PinManager.createPinBool("b", default))
) {
    override fun execute(): ExecutionState {
        if (inputs.size != outputs.size) {
            throw Exception("Number of outputs must be equal")
        }

        for (i in inputs.indices) {
            outputs[i].setValue(inputs[i].getValue())
        }
        return ExecutionState.COMPLETED
    }
}