package com.example.interpreter.models

import com.example.interpreter.blocks.BinaryOperatorBlock
import com.example.interpreter.blocks.BoolBlock
import com.example.interpreter.blocks.ForBlock
import com.example.interpreter.blocks.IfElseBlock
import com.example.interpreter.blocks.IntBlock
import com.example.interpreter.blocks.MainBlock
import com.example.interpreter.blocks.PrintBlock
import java.io.OutputStreamWriter
import java.util.concurrent.atomic.AtomicInteger
import java.io.Writer

object BlockManager {
    private val blockRegistry = mutableMapOf<String, Block>()
    private val idCounter = AtomicInteger(0)

    private fun generateId(): Id {
        return Id("block-${idCounter.getAndIncrement()}")
    }

    fun getAllBlocks() = blockRegistry.values.toList()

    fun getBlock(id: Id) = blockRegistry[id.string()]

    private fun <T : Block> createBlock(createBlockFunc: (Id) -> T): T {
        val id = generateId()
        val block = createBlockFunc(id)
        blockRegistry[id.string()] = block
        return block
    }

    internal fun createMainBlock(): MainBlock {
        val main = MainBlock()
        blockRegistry[main.id.string()] = main
        return main
    }

    fun createIfElseBlock(): Block {
        return createBlock {
            id ->
            IfElseBlock(id)
        }
    }

    fun createForBlock(): Block {
        return createBlock {
            id ->
            ForBlock(id)
        }
    }

    fun createAddBlock(): Block {
        return createBlock { id ->
            BinaryOperatorBlock(
                id, "Add",
                { a, b ->
                    when {
                        a is Int && b is Int -> a + b
                        a is Boolean && b is Boolean -> a || b
                        else -> throw IllegalArgumentException("Unsupported types for add")
                    }
                })
        }
    }

    fun createSubBlock(): Block {
        return createBlock { id ->
            BinaryOperatorBlock(
                id, "Sub",
                { a, b ->
                    when {
                        a is Int && b is Int -> a - b
                        a is Boolean && b is Boolean -> a && b
                        else -> throw IllegalArgumentException("Unsupported types for sub")
                    }
                })
        }
    }


    fun createIntBlock(value: Int = 0): Block {
        return createBlock { id -> IntBlock(id, value) }
    }

    fun createBoolBlock(value: Boolean = false): Block {
        return createBlock { id -> BoolBlock(id, value) }
    }

    fun createPrintBlock(writer: Writer = OutputStreamWriter(System.out)): Block {
        return createBlock { id -> PrintBlock(id, writer) }
    }
}