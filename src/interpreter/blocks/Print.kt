package interpreter.blocks

import interpreter.models.Block
import interpreter.models.Id
import interpreter.models.PinManager
import java.io.Writer

class PrintBlock internal constructor(
    id: Id,
    val writer: Writer
) : Block(
    id,
    "Print",
    mutableListOf(PinManager.createPinAny("a", ownId = id)),
    mutableListOf()
) {
    override fun execute() {
        inputs.forEach { input ->
            writer.write(input.getValue().toString())
            writer.write("\n")
            writer.flush()
        }
    }
}