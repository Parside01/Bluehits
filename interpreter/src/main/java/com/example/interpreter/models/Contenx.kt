package com.example.interpreter.models

import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.set

object ContextManager {
    private val contextRegistry = mutableMapOf<String, Context>()
    private val idCounter = AtomicInteger(0)

    fun createContext(startBlock: ScopeBlock): Context {
        val context = Context(startBlock)
        contextRegistry[startBlock.id.string()] = context
        return context
    }

    fun getContext(startBlockId: Id) = contextRegistry[startBlockId.string()]
}

class Context internal constructor (
    val ownBlock: ScopeBlock, // Блоки, с которых начнется выполнения контекста.
) {
    private var blockIds: MutableSet<Id> = mutableSetOf()

    init {
        // Блок, который создает новую область видимости находится в
        // контексте новой области видимости.
        blockIds.add(ownBlock.id)
        prebuild()
    }

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
        block.outputs.forEach { pin -> ConnectionManager.getPinConnections(pin).forEach {
            connection ->
            val block = connection.getTo().ownId
            result.add(block)
        } }
        return result
    }

    fun getLinkInBlocks(block: Block): Set<Id> {
        val result = mutableSetOf<Id>()
        block.inputs.forEach { pin -> ConnectionManager.getPinConnections(pin).forEach {
            connection ->
            val block = connection.getFrom().ownId
            result.add(block)
        } }
        return result
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

        return false
    }

    fun addId(id: Id) {
        blockIds.add(id)
    }
}