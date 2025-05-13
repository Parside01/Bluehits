package com.example.interpreter.blocks

import com.example.interpreter.models.Block
import com.example.interpreter.models.ExecutionState
import com.example.interpreter.models.Id
import com.example.interpreter.models.PinManager
import com.example.interpreter.models.ScopeBlock

class MainBlock :
    ScopeBlock(
        Id("main"),
        "Main",
        mutableListOf(),
        mutableListOf(PinManager.createPinBlock("body", Id("main")))
    ) {
    override fun execute(): ExecutionState {
        return ExecutionState.COMPLETED
    }
}