package com.example.interpreter.models

import com.example.interpreter.blocks.FunctionCallBlock
import com.example.interpreter.blocks.FunctionDefinitionBlock
import com.example.interpreter.blocks.FunctionReturnBlock
import jdk.internal.util.xml.impl.Input

data class FunctionInfo(
    var name: String,
    var definitionBlock: FunctionDefinitionBlock,
    val returnBlocks: MutableSet<FunctionReturnBlock>,
    val callBlocks: MutableSet<FunctionCallBlock>,
    val inputs: MutableList<Pin>,
    val outputs: MutableList<Pin>
)

object FunctionManager {
    val functions = mutableMapOf<String, FunctionInfo>()

    fun getFunctionInfo(funcName: String) = functions[funcName]

    fun addFunctionDefinitionBlock(definition: FunctionDefinitionBlock) {
        if (functions.containsKey(definition.getFunctionName())) throw IllegalArgumentException("Function definition already exists")

        val info = FunctionInfo(
            definition.name,
            definition,
            mutableSetOf(),
            mutableSetOf(),
            mutableListOf(),
            mutableListOf()
        )
        functions[definition.name] = info
    }

    fun addFunctionReturnBlock(returnBlock: FunctionReturnBlock) {
        if (!functions.containsKey(returnBlock.getFunctionName())) throw IllegalArgumentException("Function definition does not exist")
        val funcInfo = functions[returnBlock.getFunctionName()]

        funcInfo?.returnBlocks?.add(returnBlock)
        funcInfo?.outputs?.forEach { output ->
            returnBlock.addInputArg(output)
        }
    }

    fun addFunctionCallBlock(call : FunctionCallBlock) {
        if (!functions.containsKey(call.getFunctionName())) throw IllegalArgumentException("Function definition does not exist")
        functions[call.getFunctionName()]?.callBlocks?.add(call)
        functions[call.getFunctionName()]?.inputs?.forEach { input ->
            call.addInputArg(input)
        }
        functions[call.getFunctionName()]?.outputs?.forEach { output ->
            call.addOutputArg(output)
        }
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

    fun addFunctionInArg(funcName :String, arg: Pin) {
        if (!functions.containsKey(funcName)) {
            throw IllegalArgumentException("Function definition does not exist")
        }

        val info = functions[funcName]
        info?.let { info ->
            info.definitionBlock.addOutputArg(arg)
            info.callBlocks.forEach { callBlock -> callBlock.addInputArg(arg) }
            info.inputs.add(arg)
        }
    }

    fun addFunctionOutArg(funcName :String, arg: Pin) {
        if (!functions.containsKey(funcName)) {
            throw IllegalArgumentException("Function definition does not exist")
        }

        val info = functions[funcName]
        info?.let { info ->
            info.callBlocks.forEach { callBlock -> callBlock.addOutputArg(arg) }
            info.returnBlocks.forEach { returnBlock -> returnBlock.addInputArg(arg) }
            info.outputs.add(arg)
        }
    }
}