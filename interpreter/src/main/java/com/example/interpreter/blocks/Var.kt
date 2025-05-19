package com.example.interpreter.blocks

import com.example.interpreter.models.Block
import com.example.interpreter.models.ExecutionState
import com.example.interpreter.models.Id
import com.example.interpreter.models.PinManager
import com.example.interpreter.models.VarObserver
import com.example.interpreter.models.VarState
import com.example.interpreter.pins.PinBool
import com.example.interpreter.pins.PinInt

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

    internal val setPin: PinInt = inputs[0] as PinInt
    internal val getPin: PinInt = outputs[0] as PinInt

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

class BoolBlock internal constructor(
    id: Id,
    default: Boolean = false,
    name: String = "Bool"
) : Block(
    id,
    name,
    mutableListOf(PinManager.createPinBool("set", default)),
    mutableListOf(PinManager.createPinBool("get", default))
), VarObserver<Boolean> {
    lateinit var varState: VarState<Boolean>

    internal fun setVarState(varState: VarState<Boolean>) {
        this.varState = varState
    }

    val setPin: PinBool get() = inputs[0] as PinBool
    val getPin: PinBool get() = outputs[0] as PinBool

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