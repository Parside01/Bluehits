package interpreter.blocks

import interpreter.models.Block
import interpreter.models.Id
import interpreter.models.PinManager

class WhileBlock (
    id: Id
): Block(
    id,
    "While",
    mutableListOf(PinManager.createPinBool("cond", ownId = id)),
    mutableListOf(PinManager.createPinBlock("action", ownId = id))) {

    override fun execute() {

    }
}