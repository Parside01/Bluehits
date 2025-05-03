package interpreter.blocks

import interpreter.models.Block
import interpreter.models.PinManager
import java.io.Writer

class PrintBlock internal constructor(
    id: String,
    val writer: Writer
) : Block(
    id,
    "Print",
    listOf(PinManager.createPinAny("a", "null")),
    emptyList()
) {
    override fun execute() {
        inputs.forEach { input ->
            writer.write(input.getValue().toString())
            writer.write("\n")
            writer.flush()
        }
    }
}