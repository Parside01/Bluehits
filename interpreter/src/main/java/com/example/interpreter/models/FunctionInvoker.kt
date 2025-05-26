package com.example.interpreter.models

internal object FunctionInvoker {
    fun invoke(funcName: String, arguments: Map<String, Any>): Map<String, Any> {
        val functionInfo = FunctionManager.getFunctionInfo(funcName)
            ?: throw IllegalArgumentException("Function not found: $funcName")
        val definitionBlock = functionInfo.definitionBlock
        val functionContext = ContextManager.getContext(definitionBlock.id)
            ?: throw IllegalStateException("Context not found for function: $funcName")

        arguments.forEach { (arg, value) ->
            definitionBlock.pinByName(arg)?.setValue(value)
        }

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
        returnBlock?.inputs?.forEach { returnPin ->
            returnValues[returnPin.name] = returnPin.getValue()
        }
        return returnValues
    }
}