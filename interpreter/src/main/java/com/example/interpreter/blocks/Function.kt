package com.example.interpreter.blocks

import com.example.interpreter.models.ExecutionState
import com.example.interpreter.models.Id
import com.example.interpreter.models.Pin
import com.example.interpreter.models.ScopeBlock

abstract class FunctionPartBlock(
    id: Id,
    funcName: String,
    inputs: MutableList<Pin>,
    outputs: MutableList<Pin>,
) : ScopeBlock(id, funcName, inputs, outputs) {
    open fun getFunctionName(): String {
        return name
    }

    open fun setFunctionName(newName : String) {
        name = newName
    }

    open fun addInputArg(pin : Pin) {
        inputs.add(pin)
    }

    open fun addOutputArg(pin : Pin) {
        outputs.add(pin)
    }
}

class FunctionCallBlock(
    id: Id,
    funcName: String,
) : FunctionPartBlock(
    id,
    funcName,
    mutableListOf(),
    mutableListOf(),
) {
    override fun execute(): ExecutionState {
        TODO("Not yet implemented")
    }
}

class FunctionDefinitionBlock(
    id: Id,
    funcName: String
) : FunctionPartBlock(
    id,
    funcName,
    mutableListOf(),
    mutableListOf(),
) {
    override fun execute(): ExecutionState {
        TODO("Not yet implemented")
    }
}

class FunctionReturnBlock(
    id: Id,
    var funcName: String,
) : FunctionPartBlock(
    id,
    "Return",
    mutableListOf(),
    mutableListOf(),
) {
    private var executionState: ExecutionState = ExecutionState.WAITING

    fun getExecutionState() = executionState

    override fun execute(): ExecutionState {
        executionState = ExecutionState.COMPLETED
        return ExecutionState.COMPLETED
    }

    override fun rollback() {
        executionState = ExecutionState.WAITING
    }

    override fun getFunctionName(): String {
        return funcName
    }

    override fun setFunctionName(newName : String) {
        funcName = newName
    }
}