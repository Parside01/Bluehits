package interpreter.blocks

import interpreter.models.Block
import interpreter.models.ExecutionState
import interpreter.models.Id
import interpreter.models.PinManager

class ForBlock (
    id: Id
): Block(
    id,
    "For",
    mutableListOf(PinManager.createPinInt("first", ownId = id), PinManager.createPinInt("last", ownId = id), PinManager.createPinInt("step", ownId = id)),
    mutableListOf(PinManager.createPinBlock("body", ownId = id), PinManager.createPinInt("index", ownId = id)))
{
    private var currentIndex: Int? = null

    override fun execute(): ExecutionState {
        val first = pinByName("first")?.getValue() as? Int?: throw Exception("First must be an int")
        val last = pinByName("last")?.getValue() as? Int?: throw Exception("Last must be an int")
        val step = pinByName("step")?.getValue() as? Int?: throw Exception("Step must be an int")

        currentIndex = (currentIndex ?: first) + step
        if (currentIndex!! < last) {
            return ExecutionState.RUNNING
        }
        return ExecutionState.COMPLETED
    }
}