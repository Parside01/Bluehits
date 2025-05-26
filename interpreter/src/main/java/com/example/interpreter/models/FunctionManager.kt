package com.example.interpreter.models

import com.example.interpreter.blocks.FunctionCallBlock
import com.example.interpreter.blocks.FunctionDefinitionBlock
import com.example.interpreter.blocks.FunctionReturnBlock

internal data class FunctionInfo(
    var name: String,
    var definitionBlock: FunctionDefinitionBlock,
    val returnBlocks: MutableSet<FunctionReturnBlock>,
    val callBlocks: MutableSet<FunctionCallBlock>,
)

internal object FunctionManager {
    val functions = mutableMapOf<String, FunctionInfo>()

    fun getFunctionInfo(funcName: String) = functions[funcName]

    fun addFunctionDefinitionBlock(definition: FunctionDefinitionBlock) {
        if (functions.containsKey(definition.getFunctionName())) throw IllegalArgumentException("Function definition already exists")

        val info = FunctionInfo(
            definition.name,
            definition,
            mutableSetOf(),
            mutableSetOf()
        )
        functions[definition.name] = info
    }

    fun addFunctionReturnBlock(returnBlock: FunctionReturnBlock) {
        if (!functions.containsKey(returnBlock.getFunctionName())) throw IllegalArgumentException("Function definition does not exist")
        functions[returnBlock.getFunctionName()]?.returnBlocks?.add(returnBlock)
    }

    fun addFunctionCallBlock(call : FunctionCallBlock) {
        if (!functions.containsKey(call.getFunctionName())) throw IllegalArgumentException("Function definition does not exist")
        functions[call.getFunctionName()]?.callBlocks?.add(call)
    }

    fun renameFunction(funcName: String, newName: String) {
        if (!functions.containsKey(funcName)) {
            throw IllegalArgumentException("Function definition does not exist")
        }
        if (functions.containsKey(newName)) {
            throw IllegalArgumentException("Function definition already exists")
        }
        val info = functions[funcName]
        info?.let { info ->
            info.name = newName
            info.definitionBlock.setFunctionName(newName)
            info.returnBlocks.forEach { returnBlock -> returnBlock.setFunctionName(newName) }
            info.callBlocks.forEach { callBlock -> callBlock.setFunctionName(newName) }

            functions.remove(funcName)
            functions[newName] = info
        }
    }
}