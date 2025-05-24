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
        return ExecutionState.COMPLETED
    }

    override fun onValueChanged(newValue: Boolean) {
        getPin.setValue(newValue)
        setPin.setValue(newValue)
    }
}

@Suppress("UNCHECKED_CAST")
class ArrayBlock internal constructor(
    id: Id,
    default: List<Any> = mutableListOf(),
    name: String = "Array"
) : Block(
    id,
    name,
    mutableListOf(PinManager.createPinArray("set", default, ownId = id)),
    mutableListOf(PinManager.createPinArray("get", default, ownId = id))
), VarObserver<List<Any>> {
    lateinit var varState: VarState<List<Any>>

    internal fun setVarState(varState: VarState<List<Any>>) {
        this.varState = varState
    }

    internal val setPin: TPin<List<Any>> get() = inputs[0] as TPin<List<Any>>
    internal val getPin: TPin<List<Any>> get() = outputs[0] as TPin<List<Any>>

    override fun execute(): ExecutionState {
        if (!::varState.isInitialized) {
            throw IllegalStateException("ArrayBlock '${name}' (ID: ${id.string()}) not initialized.")
        }

        val inputValue = setPin.getValue() as List<Any>
        varState.setValue(inputValue)
        return ExecutionState.COMPLETED
    }

    override fun onValueChanged(newValue: List<Any>) {
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
        return ExecutionState.COMPLETED
    }

    override fun onValueChanged(newValue: Float) {
        getPin.setValue(newValue)
        setPin.setValue(newValue)
    }
}