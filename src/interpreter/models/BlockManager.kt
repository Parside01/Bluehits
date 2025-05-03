package interpreter.models

import interpreter.blocks.BinaryOperatorBlock
import interpreter.blocks.BoolBlock
import interpreter.blocks.IntBlock
import interpreter.blocks.PrintBlock
import java.io.OutputStreamWriter
import java.util.concurrent.atomic.AtomicInteger
import java.io.Writer

object BlockManager {
    private val blockRegistry = mutableMapOf<String, Block>()
    private val idCounter = AtomicInteger(0)


    private fun generateId(): String {
        return "block-${idCounter.getAndIncrement()}"
    }

    fun getBlock(id: String) = blockRegistry[id]

    private fun <T : Block> createBlock(createBlockFunc: (String) -> T): T {
        val id = generateId()
        val block = createBlockFunc(id)
        blockRegistry[id] = block
        return block
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