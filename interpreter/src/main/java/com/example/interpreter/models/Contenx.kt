package com.example.interpreter.models

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

    fun deleteAll() {
        contextRegistry.clear()
        idCounter.getAndDecrement()
        ctxParents.clear()
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
            findContextBlocks(id)
        }
    }

    fun rollback() {
        blockIds.forEach { id ->
            BlockManager.getBlock(id)?.rollback()
        }
        ConnectionManager.rollback()
    }

    private fun findContextBlocks(currBlock: Id) {
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
                    findContextBlocks(id)
                }
            }
        }
    }

    private fun getLinkOutBlocks(block: Block): Set<Id> {
        val result = mutableSetOf<Id>()
        block.outputs.forEach { pin ->
            ConnectionManager.getPinConnections(pin).forEach { connection ->
                val connectedBlock = connection.getTo().ownId
                result.add(connectedBlock)
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
        return block.outputs
            .filter { !it.isDisabled() }
            .flatMap { pin -> ConnectionManager.getPinConnections(pin) }
    }

    fun getBlockInConnections(block: Block): List<Connection> {
        val connections = mutableListOf<Connection>()
        connections.addAll(
            block.inputs
                .filter { !it.isDisabled() }
                .flatMap { pin -> ConnectionManager.getPinConnections(pin) })

        if (!block.blockPin.isDisabled()) {
            connections.addAll(ConnectionManager.getPinConnections(block.blockPin))
        }
        return connections
    }

    fun execute(): Boolean {
        var isCompleted = false


        isCompleted = ownBlock.execute() == ExecutionState.COMPLETED

        getBlockOutConnections(ownBlock).forEach { connection -> connection.execute() }


        val executionQueue: MutableList<Block> = mutableListOf()
        val executeSet: MutableSet<Id> = mutableSetOf()

        getBlockOutConnections(ownBlock).forEach { conn ->
            conn.execute()
            val nextId = conn.getTo().ownId
            if (executeSet.add(nextId)) {
                BlockManager.getBlock(nextId)?.let { block -> executionQueue.add(block) }
            }
        }

        while (executionQueue.isNotEmpty()) {
            val currentBlock = executionQueue.removeAt(0)

            val inConnections = Program.getBlockInConnections(currentBlock)
            val connIsExecuted = inConnections.all { it.executed() }

            if (!connIsExecuted) {
                inConnections.forEach { conn ->
                    if (!conn.executed()) {
                        val fromId = conn.getFrom().ownId
                        BlockManager.getBlock(fromId)?.let { block ->
                            if (!executeSet.contains(block.id)) {
                                executionQueue.add(0, block)
                            }
                        }
                    }
                }
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

        rollback() // Уф
        return isCompleted
    }

    fun addId(id: Id) {
        blockIds.add(id)
    }
}