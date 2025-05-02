package blocks

import model.Block
import model.Pin

class PrintBlock(
    id: String,
    name: String,
    inputs: List<Pin> = emptyList(),
    outputs: List<Pin> = emptyList()
) : Block(id, name, inputs, outputs) {
    override fun execute() {
        println("Hello world!")
    }
}