package interpreter.blocks

import interpreter.models.Block
import interpreter.models.Connection
import interpreter.models.PinManager

class IntBlock internal constructor(
    id: String,
    default: Int = 0
) : Block(
    id,
    "Int",
    listOf(PinManager.createPinInt("a", default)),
    listOf(PinManager.createPinInt("b", default))
) {
    override fun execute() {
        if (inputs.size != outputs.size) {
            throw Exception("Number of outputs must be equal")
        }

        for (i in inputs.indices) {
            outputs[i].setValue(inputs[i].getValue())
        }
    }
}

class BoolBlock internal constructor(
    id: String,
    default: Boolean = false
) : Block(
    id,
    "Bool",
    listOf(PinManager.createPinBool("a", default)),
    listOf(PinManager.createPinBool("b", default))
) {
    override fun execute() {
        if (inputs.size != outputs.size) {
            throw Exception("Number of outputs must be equal")
        }

        for (i in inputs.indices) {
            outputs[i].setValue(inputs[i].getValue())
        }
    }
}