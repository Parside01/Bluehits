package interpreter.models

object Program  {
    fun run() {

    }

    fun getBlockOutConnections(block: Block): List<Connection> {
        return block.outputs.flatMap { pin -> ConnectionManager.getPinConnections(pin) }
    }

    fun getBlockInConnections(block: Block): List<Connection> {
        return block.inputs.flatMap { pin -> ConnectionManager.getPinConnections(pin) }
    }

    fun validateBlock(block: Block) {
        block.inputs.forEach { pin ->
            val connections = ConnectionManager.getPinConnections(pin)
            if (connections.size > 1) {
                throw IllegalStateException("Input pin ${pin.name} in block ${block.name} has more than one connection: ${connections.size}")
            }
        }
    }
}