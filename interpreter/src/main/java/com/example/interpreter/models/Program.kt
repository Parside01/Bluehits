package com.example.interpreter.models

import com.example.interpreter.blocks.MainBlock
import kotlin.collections.forEach

object Program {
    private var mainBlock: MainBlock = BlockManager.createMainBlock()

    fun getMainBlock(): Block {
        return mainBlock
    }

    fun run() {
        prebuild()
        ContextManager.getContext(mainBlock.id)!!.execute()
    }

    fun prebuild() {
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