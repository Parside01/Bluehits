package com.example.interpreter.models

import com.example.interpreter.blocks.AppendBlock
import com.example.interpreter.blocks.ArrayBlock
import com.example.interpreter.blocks.BinaryOperatorBlock
import com.example.interpreter.blocks.BoolBlock
import com.example.interpreter.blocks.FloatBlock
import com.example.interpreter.blocks.ForBlock
import com.example.interpreter.blocks.IfElseBlock
import com.example.interpreter.blocks.IndexBlock
import com.example.interpreter.blocks.IntBlock
import com.example.interpreter.blocks.MainBlock
import com.example.interpreter.blocks.PrintBlock
import java.io.OutputStreamWriter
import java.util.concurrent.atomic.AtomicInteger
import java.io.Writer
import kotlin.reflect.KClass

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
        return createBlock { id ->
            IfElseBlock(id)
        }
    }

    fun createForBlock(): Block {
        return createBlock { id ->
            ForBlock(id)
        }
    }

    fun <T : Number> createAddBlock(type: KClass<T>): Block {
        return createBlock { id ->
            BinaryOperatorBlock(
                id, "Add",
                { a, b ->
                    when {
                        a.javaClass == type.java && b.javaClass == type.java -> {
                            when (type) {
                                Int::class -> (a as Int) + (b as Int)
                                Double::class -> (a as Double) + (b as Double)
                                Float::class -> (a as Float) + (b as Float)
                                Long::class -> (a as Long) + (b as Long)
                                else -> throw IllegalArgumentException("Unsupported type for add: $type")
                            } as T
                        }
                        else -> throw IllegalArgumentException("Unsupported types for add: ${a?.javaClass}, ${b?.javaClass}")
                    }
                },
                type = type
            )
        }
    }

    fun <T : Number> createSubBlock(type: KClass<T>): Block {
        return createBlock { id ->
            BinaryOperatorBlock(
                id, "Add",
                { a, b ->
                    when {
                        a.javaClass == type.java && b.javaClass == type.java -> {
                            when (type) {
                                Int::class -> (a as Int) - (b as Int)
                                Double::class -> (a as Double) - (b as Double)
                                Float::class -> (a as Float) - (b as Float)
                                Long::class -> (a as Long) - (b as Long)
                                else -> throw IllegalArgumentException("Unsupported type for sub: $type")
                            } as T
                        }
                        else -> throw IllegalArgumentException("Unsupported types for sub: ${a.javaClass}, ${b.javaClass}")
                    }
                },
                type = type
            )
        }
    }

    fun createIntBlock(varName: String = "Int", value: Int = 0): Block {
        val block = createBlock { id -> IntBlock(id, value, varName) }
        val blockState = VariableManager.getOrCreateVarState(varName, value, Int::class)
        block.setVarState(blockState)

        blockState.addObserver(block)
        block.setPin.setValue(blockState.getValue() as Any)

        return block
    }

    fun createBoolBlock(varName: String = "Bool", value: Boolean = false): Block {
        val block = createBlock { id -> BoolBlock(id, value, varName) }

        val blockState = VariableManager.getOrCreateVarState(varName, value, Boolean::class)
        block.setVarState(blockState)

        blockState.addObserver(block)
        block.setPin.setValue(blockState.getValue() as Any)

        return block
    }

    fun createFloatBlock(varName: String = "Float", value: Float = Utils.getDefaultValue(Float::class.java)): Block {
        val block = createBlock { id -> FloatBlock(id, value, varName) }

        val blockState = VariableManager.getOrCreateVarState(varName, value, Float::class)
        block.setVarState(blockState)

        blockState.addObserver(block)
        block.setPin.setValue(blockState.getValue() as Any)

        return block
    }

    @Suppress("UNCHECKED_CAST")
    fun createArrayBlock(varName: String = "Array", value: List<Any> = emptyList()): Block {
        val block = createBlock { id -> ArrayBlock(id, value, varName) }

        val blockState = VariableManager.getOrCreateVarState(varName, value, List::class)
        block.setVarState(blockState as VarState<List<Any>>)

        blockState.addObserver(block)
        block.setPin.setValue(blockState.getValue())

        return block
    }

    fun createAppendBlock(): Block {
        return createBlock { id -> AppendBlock(id) }
    }

    fun createPrintBlock(writer: Writer = OutputStreamWriter(System.out)): Block {
        return createBlock { id -> PrintBlock(id, writer) }
    }

    fun createIndexBlock(): Block {
        return createBlock { id -> IndexBlock(id) }
    }
}