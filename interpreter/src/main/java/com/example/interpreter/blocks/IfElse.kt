package com.example.interpreter.blocks

import com.example.interpreter.models.Block
import com.example.interpreter.models.ExecutionState
import com.example.interpreter.models.Id
import com.example.interpreter.models.Pin
import com.example.interpreter.models.PinManager

class IfElseBlock(
    id: Id,
) : Block(
    id,
    "IfElse",
    mutableListOf(PinManager.createPinBool("a", ownId = id)),
    mutableListOf(PinManager.createPinBlock("if", ownId = id), PinManager.createPinBlock("else", ownId = id)),
) {

    override fun execute(): ExecutionState {
        val condition = inputs.single().getValue() as Boolean
        outputs[0].disable()
        if (condition) {
            outputs[1].enable()
        } else {
            outputs[0].enable()
            outputs[1].disable()
        }
        return ExecutionState.COMPLETED
    }

    override fun rollback() {
        outputs.forEach { it.enable() }
    }
}