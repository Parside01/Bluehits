package interpreter.blocks

import interpreter.models.Block
import interpreter.models.Id
import interpreter.models.PinManager
import java.io.Writer

class IfElseBlock(
    id: Id,
) : Block(
    id,
    "IfElse",
    mutableListOf(PinManager.createPinBool("a")),
    mutableListOf(PinManager.createPinBlock("if", ownId = id), PinManager.createPinBlock("else", ownId = id)),
) {
    override fun execute() {
        if (inputs.single().getValue() == true) {
            outputs[1] = outputs[0]
        } else {
            outputs[0] = outputs[1]
        }
    }
}