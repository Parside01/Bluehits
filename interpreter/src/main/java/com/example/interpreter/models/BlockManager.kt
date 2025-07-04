package com.example.interpreter.models

import com.example.interpreter.blocks.AppendBlock
import com.example.interpreter.blocks.ArrayBlock
import com.example.interpreter.blocks.BinaryLogicOperatorBlock
import com.example.interpreter.blocks.BinaryOperatorBlock
import com.example.interpreter.blocks.BoolBlock
import com.example.interpreter.blocks.CastBlock
import com.example.interpreter.blocks.FloatBlock
import com.example.interpreter.blocks.ForBlock
import com.example.interpreter.blocks.FunctionCallBlock
import com.example.interpreter.blocks.FunctionDefinitionBlock
import com.example.interpreter.blocks.FunctionReturnBlock
import com.example.interpreter.blocks.IfElseBlock
import com.example.interpreter.blocks.IndexBlock
import com.example.interpreter.blocks.IntBlock
import com.example.interpreter.blocks.MainBlock
import com.example.interpreter.blocks.MathBlock
import com.example.interpreter.blocks.PrintBlock
import com.example.interpreter.blocks.StringBlock
import com.example.interpreter.blocks.SwapBlock
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

    fun <T : Block> createBlock(createBlockFunc: (Id) -> T): T {
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

    fun <T : Number> createGreaterBlock(type: KClass<T>): Block {
        return createBlock { id ->
            BinaryLogicOperatorBlock(
                id, ">",
                { a, b ->
                    when (type) {
                        Int::class -> (a as Int) > (b as Int)
                        Double::class -> (a as Double) > (b as Double)
                        Float::class -> (a as Float) > (b as Float)
                        Long::class -> (a as Long) > (b as Long)
                        else -> throw IllegalArgumentException("Unsupported type for add: $type")
                    }
                },
                type = type
            )
        }
    }

    fun <T : Number> createAddBlock(type: KClass<T>): Block {
        return createBlock { id ->
            BinaryOperatorBlock(
                id, "Add",
                { a, b ->
                    when (type) {
                        Int::class -> (a as Int) + (b as Int)
                        Double::class -> (a as Double) + (b as Double)
                        Float::class -> (a as Float) + (b as Float)
                        Long::class -> (a as Long) + (b as Long)
                        else -> throw IllegalArgumentException("Unsupported type for add: $type")
                    } as T
                },
                type = type
            )
        }
    }

    inline fun <reified T : Any> createSwapBlock(): Block {
        return createBlock { id -> SwapBlock(id, T::class) }
    }

    inline fun <reified T : Any> createIndexBlock(): Block {
        return createBlock { id -> IndexBlock(id, T::class) }
    }

    inline fun <reified T : Any> createAppendBlock(): Block {
        return createBlock { id -> AppendBlock(id, T::class) }
    }

    fun <T : Number> createSubBlock(type: KClass<T>): Block {
        return createBlock { id ->
            BinaryOperatorBlock(
                id, "Sub",
                { a, b ->
                    when (type) {
                        Int::class -> (a as Int) - (b as Int)
                        Double::class -> (a as Double) - (b as Double)
                        Float::class -> (a as Float) - (b as Float)
                        Long::class -> (a as Long) - (b as Long)
                        else -> throw IllegalArgumentException("Unsupported type for sub: $type")
                    } as T
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

    fun createFloatBlock(
        varName: String = "Float",
        value: Float = Utils.getDefaultValue(Float::class.java)
    ): Block {
        val block = createBlock { id -> FloatBlock(id, value, varName) }

        val blockState = VariableManager.getOrCreateVarState(varName, value, Float::class)
        block.setVarState(blockState)

        blockState.addObserver(block)
        block.setPin.setValue(blockState.getValue() as Any)

        return block
    }

    fun createStringBlock(
        varName: String = "String",
        value: String = Utils.getDefaultValue(String::class.java)
    ): Block {
        val block = createBlock { id -> StringBlock(id, value, varName) }

        val blockState = VariableManager.getOrCreateVarState(varName, value, String::class)
        block.setVarState(blockState)

        blockState.addObserver(block)
        block.setPin.setValue(blockState.getValue() as Any)

        return block
    }

    fun <F:Any, T:Any> createCastBlock(typeFrom: KClass<F>, typeTo: KClass<in T>): Block {
        return createBlock { id -> CastBlock(id, typeFrom, typeTo) }
    }

    fun <T : Any> createArrayBlock(
        varName: String = "Array",
        value: List<T> = emptyList(),
        elementType: KClass<T>,
    ): Block {
        val block = createBlock { id -> ArrayBlock(id, value as List<T>, varName, elementType = elementType) }

        val blockState = VariableManager.getOrCreateVarState(varName, value as List<T>, List::class)
        block.setVarState(blockState as VarState<List<T>>)

        blockState.addObserver(block)
        block.setPin.setValue(blockState.getValue())

        return block
    }

    fun createPrintBlock(writer: Writer = OutputStreamWriter(System.out)): Block {
        return createBlock { id -> PrintBlock(id, writer) }
    }

    fun createFunctionDefinitionBlock(funcName: String) : Block {
        val block = createBlock { id -> FunctionDefinitionBlock(funcName = funcName, id = id) }
        FunctionManager.addFunctionDefinitionBlock(block)
        return block
    }

    fun createFunctionCalledBlock(funcName: String) : Block {
        val block = createBlock { id -> FunctionCallBlock(id, funcName) }
        FunctionManager.addFunctionCallBlock(block)
        return block
    }

    fun createFunctionReturnBlock(funcName: String) : Block {
        val block = createBlock { id -> FunctionReturnBlock(id, funcName) }
        FunctionManager.addFunctionReturnBlock(block)
        return block
    }

    fun createMathBlock(): Block {
        return createBlock { id -> MathBlock(id) }
    }

}