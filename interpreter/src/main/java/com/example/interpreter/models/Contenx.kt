package com.example.interpreter.models

import com.example.interpreter.models.Program.getBlockOutConnections
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.set


object ContextManager {
    private val contextRegistry = mutableMapOf<String, Context>()
    private val idCounter = AtomicInteger(0)

    // Это надо для иерархии контекстов.
    private val ctxParents = mutableMapOf<String, String>();

    fun createContext(startBlock: ScopeBlock): Context {
        val context = Context(startBlock)
        contextRegistry[startBlock.id.string()] = context
        return context
    }

    fun getContext(startBlockId: Id) = contextRegistry[startBlockId.string()]
}

class Context internal constructor(
    val ownBlock: ScopeBlock, // Блоки, с которых начнется выполнения контекста.
) {
    private var blockIds: MutableSet<Id> = mutableSetOf()

    init {
        // Блок, который создает новую область видимости находится в
        // контексте новой области видимости. Ну и да...
        blockIds.add(ownBlock.id)
        prebuild()
    }

    fun blocksList() = blockIds.toList()

    fun prebuild() {
        val ids = getLinkOutBlocks(ownBlock)
        ids.forEach { id ->
            travelContextBlocks(id)
        }
    }

    private fun travelContextBlocks(currBlock: Id) {
        if (blockIds.contains(currBlock)) {
            return
        }

        val currentBlock = BlockManager.getBlock(currBlock)
        if (currentBlock == null) {
            return
        }

        // В одном контексте не может быть больше одного такого блока.
        if (currentBlock !is ScopeBlock) {
            blockIds.add(currBlock)
        }

        val linkedOutIds = getLinkOutBlocks(currentBlock)
        val linkedInIds = getLinkInBlocks(currentBlock)

        val allConnectedIds = linkedOutIds + linkedInIds

        allConnectedIds.forEach { id ->
            BlockManager.getBlock(id)?.let { block ->
                if (block !is ScopeBlock) {
                    travelContextBlocks(id)
                }
            }
        }
    }

    fun getLinkOutBlocks(block: Block): Set<Id> {
        val result = mutableSetOf<Id>()
        block.outputs.forEach { pin ->
            ConnectionManager.getPinConnections(pin).forEach { connection ->
                val block = connection.getTo().ownId
                result.add(block)
            }
        }
        return result
    }

    fun getLinkInBlocks(block: Block): Set<Id> {
        val result = mutableSetOf<Id>()
        block.inputs.forEach { pin ->
            ConnectionManager.getPinConnections(pin).forEach { connection ->
                val block = connection.getFrom().ownId
                result.add(block)
            }
        }
        return result
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

    fun execute(): Boolean {
        var isCompleted = false

        val startBlocks = mutableListOf<Block>()
        blockIds.forEach { id ->
            val block = BlockManager.getBlock(id)
            block?.let { block ->
                if (getBlockInConnections(block).isEmpty()) {
                    startBlocks.add(block)
                }
            }
        }

        val startOutConnections = mutableListOf<Connection>()
        startOutConnections.addAll(getBlockOutConnections(ownBlock))
        startOutConnections.forEach { connection -> connection.execute() }

        // Вообще надо договоренность, что только ScopeBlockи могут возвращать RUNNING
        isCompleted = ownBlock.execute() == ExecutionState.COMPLETED
        startBlocks.forEach { block ->
            block.execute()
            startOutConnections.addAll(getBlockOutConnections(block))
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
            val currentBlock = executionQueue.removeAt(0)

            val inConnections = Program.getBlockInConnections(currentBlock)
            var connIsExecuted = true
            inConnections.forEach { conn ->
                connIsExecuted = conn.executed()
            }
            if (!connIsExecuted) {
                executionQueue.add(currentBlock)
                continue
            }


            // Если мы встретили такой блок, то мы просто исполним его контекст и все.
            if (currentBlock is ScopeBlock) {
                var executed = false
                while (!executed) {
                    executed = ContextManager.getContext(currentBlock.id)?.execute() == true
                }
                continue
            }

            currentBlock.execute()
            val blockOutConn = Program.getBlockOutConnections(currentBlock)
            blockOutConn.forEach { conn ->
                conn.execute()
                val nextId = conn.getTo().ownId
                if (executeSet.add(nextId)) {
                    BlockManager.getBlock(nextId)?.let { block -> executionQueue.add(block) }
                }
            }
        }

        return isCompleted
    }

    fun addId(id: Id) {
        blockIds.add(id)
    }
}