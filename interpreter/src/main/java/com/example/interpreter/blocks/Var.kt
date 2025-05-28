package com.example.interpreter.blocks

import com.example.interpreter.models.Block
import com.example.interpreter.models.ExecutionState
import com.example.interpreter.models.Id
import com.example.interpreter.models.TPin
import com.example.interpreter.models.PinManager
import com.example.interpreter.models.Utils
import com.example.interpreter.models.VarObserver
import com.example.interpreter.models.VarState

@Suppress("UNCHECKED_CAST")
class IntBlock internal constructor(
    id: Id,
    default: Int = 0,
    name: String = "Int"
) : Block(
    id,
    name,
    mutableListOf(PinManager.createPinInt("a", default, id)),
    mutableListOf(PinManager.createPinInt("b", default, id))
), VarObserver<Int> {
    lateinit var varState: VarState<Int>
    internal fun setVarState(varState: VarState<Int>) {
        this.varState = varState
    }

    internal val setPin: TPin<Int> = inputs[0] as TPin<Int>
    internal val getPin: TPin<Int> = outputs[0] as TPin<Int>

    override fun execute(): ExecutionState {
        if (!::varState.isInitialized) {
            throw IllegalStateException("IntVarBlock '${name}' (ID: ${id.string()}) not initialized.")
        }
        val inputValue = setPin.getValue() as Int
        varState.setValue(inputValue)
        getPin.setValue(inputValue)
        return ExecutionState.COMPLETED
    }

    override fun onValueChanged(newValue: Int) {
        getPin.setValue(newValue)
        setPin.setValue(newValue)
    }
}

@Suppress("UNCHECKED_CAST")
class BoolBlock internal constructor(
    id: Id,
    default: Boolean = false,
    name: String = "Bool"
) : Block(
    id,
    name,
    mutableListOf(PinManager.createPinBool("set", default, ownId = id)),
    mutableListOf(PinManager.createPinBool("get", default, ownId = id))
), VarObserver<Boolean> {
    lateinit var varState: VarState<Boolean>

    internal fun setVarState(varState: VarState<Boolean>) {
        this.varState = varState
    }

    internal val setPin: TPin<Boolean> get() = inputs[0] as TPin<Boolean>
    internal val getPin: TPin<Boolean> get() = outputs[0] as TPin<Boolean>

    override fun execute(): ExecutionState {
        if (!::varState.isInitialized) {
            throw IllegalStateException("BoolBlock '${name}' (ID: ${id.string()}) not initialized.")
        }

        val inputValue = setPin.getValue() as Boolean
        varState.setValue(inputValue)
        getPin.setValue(inputValue)
        return ExecutionState.COMPLETED
    }

    override fun onValueChanged(newValue: Boolean) {
        getPin.setValue(newValue)
        setPin.setValue(newValue)
    }
}

class ArrayBlock<T> internal constructor(
    id: Id,
    default: List<T> = mutableListOf(),
    name: String = "Array"
) : Block(
    id,
    name,
    mutableListOf(PinManager.createPinArray("set", default, ownId = id)),
    mutableListOf(PinManager.createPinArray("get", default, ownId = id))
), VarObserver<List<T>> {
    lateinit var varState: VarState<List<T>>

    internal fun setVarState(varState: VarState<List<T>>) {
        this.varState = varState
    }

    internal val setPin: TPin<List<T>> get() = inputs[0] as TPin<List<T>>
    internal val getPin: TPin<List<T>> get() = outputs[0] as TPin<List<T>>

    override fun execute(): ExecutionState {
        if (!::varState.isInitialized) {
            throw IllegalStateException("ArrayBlock '${name}' (ID: ${id.string()}) not initialized.")
        }

        val inputValue = setPin.getValue()
        varState.setValue(inputValue as List<T>)
        getPin.setValue(inputValue)
        return ExecutionState.COMPLETED
    }

    fun append(value: Any) {
        var arr = setPin.getValue() as List<T>
        arr = arr.toMutableList()
        arr.add(value as T)
        setPin.setValue(arr)
    }

    fun index(index: Int): T {
        return (setPin.getValue() as List<T>).elementAt(index) as T
    }

    override fun onValueChanged(newValue: List<T>) {
        getPin.setValue(newValue)
        setPin.setValue(newValue)
    }
}

@Suppress("UNCHECKED_CAST")
class FloatBlock internal constructor(
    id: Id,
    default: Float = Utils.getDefaultValue(Float::class.java),
    name: String = "Float"
) : Block(
    id,
    name,
    mutableListOf(PinManager.createPinFloat("set", default, ownId = id)),
    mutableListOf(PinManager.createPinFloat("get", default, ownId = id))
), VarObserver<Float> {
    lateinit var varState: VarState<Float>

    internal fun setVarState(varState: VarState<Float>) {
        this.varState = varState
    }

    internal val setPin: TPin<Float> get() = inputs[0] as TPin<Float>
    internal val getPin: TPin<Float> get() = outputs[0] as TPin<Float>

    override fun execute(): ExecutionState {
        if (!::varState.isInitialized) {
            throw IllegalStateException("FloatBlock '${name}' (ID: ${id.string()}) not initialized.")
        }

        val inputValue = setPin.getValue() as Float
        varState.setValue(inputValue)
        getPin.setValue(inputValue)

        return ExecutionState.COMPLETED
    }

    override fun onValueChanged(newValue: Float) {
        getPin.setValue(newValue)
        setPin.setValue(newValue)
    }
}

class StringBlock internal constructor(
    id: Id,
    default: String = "",
    name: String = "String"
) : Block(
    id,
    name,
    mutableListOf(PinManager.createPinString("set", default, ownId = id)),
    mutableListOf(PinManager.createPinString("get", default, ownId = id))
), VarObserver<String> {
    lateinit var varState: VarState<String>

    internal fun setVarState(varState: VarState<String>) {
        this.varState = varState
    }

    internal val setPin: TPin<String> get() = inputs[0] as TPin<String>
    internal val getPin: TPin<String> get() = outputs[0] as TPin<String>

    override fun execute(): ExecutionState {
        if (!::varState.isInitialized) {
            throw IllegalStateException("StringBlock '${name}' (ID: ${id.string()}) not initialized.")
        }

        val inputValue = setPin.getValue() as String
        varState.setValue(inputValue)
        getPin.setValue(inputValue)

        return ExecutionState.COMPLETED
    }

    override fun onValueChanged(newValue: String) {
        getPin.setValue(newValue)
        setPin.setValue(newValue)
    }
}