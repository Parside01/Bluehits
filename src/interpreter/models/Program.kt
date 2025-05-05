package interpreter.models

object Program {
    // TODO: Щас пока делаем базу, потом надо при необходимости оптимизировать.
    fun run() {
        try {
            // Стартовые блоки - у которых нет входных пинов.
            val startBlocks = BlockManager.getAllBlocks().filter { block -> getBlockInConnections(block).isEmpty() }
            startBlocks.forEach { startBlock -> startBlock.execute() }

            // Выполняем связи.
            startBlocks.forEach { startBlock -> getBlockOutConnections(startBlock).forEach { outConnection -> outConnection.execute() } }
        } catch (e: Exception) {
            throw e
        }
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