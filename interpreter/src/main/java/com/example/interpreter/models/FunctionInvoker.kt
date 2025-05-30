package com.example.interpreter.models

internal object FunctionInvoker {
    fun invoke(funcName: String, arguments: Map<String, Any>): Map<String, Any> {
        val functionInfo = FunctionManager.getFunctionInfo(funcName)
            ?: throw IllegalArgumentException("Function not found: $funcName")
        val definitionBlock = functionInfo.definitionBlock

        arguments.forEach { (arg, value) ->
            definitionBlock.pinByName(arg)?.setValue(value)
        }

        // TODO: Вынести этот блок кода в ContextManager
        var executed = false
        while (!executed) {
            executed = ContextManager.getContext(definitionBlock.id)?.execute() == true
        }

        val returnValues = mutableMapOf<String, Any>()

        if (functionInfo.returnBlocks.count { returnBlock ->
                returnBlock.getExecutionState() == ExecutionState.COMPLETED
            } > 1) {
            throw IllegalStateException("Only one return can execute in $funcName")
        }

        val returnBlock = functionInfo.returnBlocks.firstOrNull { block ->
            block.getExecutionState() == ExecutionState.COMPLETED
        }

        returnBlock?.inputs?.forEach { res ->
            returnValues[res.name] = res.getValue()
        }

        functionInfo.returnBlocks.forEach { returnBlock ->
            returnBlock.setExecutionState(ExecutionState.WAITING)
        }

        return returnValues
    }
}