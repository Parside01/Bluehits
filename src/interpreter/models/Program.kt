package interpreter.models

object Program {
    // TODO: Щас пока делаем базу, потом надо при необходимости оптимизировать.
    // TODO: Щас на ui по идее что-то может поломаться. Можно попробовать это решить. Создавать промежуточные снимки программы через новые объекты менеджеров, а потом их подменять.
    fun run() {
        try {
            // Стартовые блоки - у которых нет входных пинов.
            val startBlocks = BlockManager.getAllBlocks().filter { block -> getBlockInConnections(block).isEmpty() }
            startBlocks.forEach { startBlock -> startBlock.execute() }

            // Выполняем связи.
            val startOutConnections = mutableListOf<Connection>()
            startBlocks.forEach { startBlock ->
                val outConnections = getBlockOutConnections(startBlock)
                startOutConnections.addAll(outConnections)
                outConnections.forEach { outConnection -> outConnection.execute() }
            }

            val executionQueue: MutableList<Block> = mutableListOf()
            val executeSet: MutableSet<Id> = mutableSetOf()

            startOutConnections.forEach { conn ->
                val nextId = conn.getTo().ownId
                if (executeSet.add(nextId)) {
                    BlockManager.getBlock(nextId)?.let { block -> executionQueue.add(block) }
                }
            }

            while (executionQueue.isNotEmpty()) {
                val currentBlock = executionQueue.removeFirst()

                val inConnections = getBlockInConnections(currentBlock)
                var connIsExecuted = true
                inConnections.forEach { conn ->
                    connIsExecuted = conn.executed()
                }
                if (!connIsExecuted) {
                    executionQueue.add(currentBlock)
                    continue
                }

                currentBlock.execute()
                val blockOutConn = getBlockOutConnections(currentBlock)
                blockOutConn.forEach { conn ->
                    conn.execute()
                    val nextId = conn.getTo().ownId
                    if (executeSet.add(nextId)) {
                        BlockManager.getBlock(nextId)?.let { block -> executionQueue.add(block) }
                    }
                }
            }

        } catch (e: Exception) {
            throw e
        }
    }

    fun getBlockOutConnections(block: Block): List<Connection> {
        return block.outputs.flatMap { pin -> ConnectionManager.getPinConnections(pin) }
    }

    fun getBlockInConnections(block: Block): List<Connection> {
        val connections = mutableListOf<Connection>()
        connections.addAll(block.inputs.flatMap { pin ->
            ConnectionManager.getPinConnections(pin)
        })
        connections.addAll(ConnectionManager.getPinConnections(block.blockPin))
        return connections
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