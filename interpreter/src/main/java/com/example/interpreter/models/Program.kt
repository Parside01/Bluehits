package com.example.interpreter.models

import com.example.interpreter.blocks.MainBlock
import kotlin.collections.forEach

object Program {
    private var mainBlock: MainBlock = BlockManager.createMainBlock()
    @Volatile var isRunning: Boolean = false
        private set
    @Volatile var shouldStop: Boolean = false

    fun getMainBlock(): Block {
        return mainBlock
    }

    fun stop() {
        shouldStop = true
        ContextManager.getAllContexts().forEach { context -> context.rollback() }
    }

    suspend fun run() {
        isRunning = true
        shouldStop = false
        prebuild()
        try {
            ContextManager.getContext(mainBlock.id)?.execute()
        } finally {
            isRunning = false
            shouldStop = false
        }
    }

    fun prebuild() {
        ContextManager.deleteAll()

        val contexts = mutableListOf<Context>()
        val allScopeBlocks = BlockManager.getAllBlocks().filter { block -> block is ScopeBlock  }
        allScopeBlocks.forEach { block ->
            ContextManager.createContext(block as ScopeBlock)
        }

        // Тут мы просто потом собираем мапку для контекстов Контекст:Родитель или пустота.
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