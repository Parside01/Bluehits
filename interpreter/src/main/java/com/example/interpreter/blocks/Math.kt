package com.example.interpreter.blocks

import com.example.interpreter.math.MathInterpreter
import com.example.interpreter.models.Block
import com.example.interpreter.models.ExecutionState
import com.example.interpreter.models.Id
import com.example.interpreter.models.PinManager

class MathBlock(
    id: Id,
) : Block(
    id,
    "Math",
    mutableListOf(PinManager.createPinString("expression", ownId = id)),
    mutableListOf(PinManager.createPinFloat("result", ownId = id)),
) {

    override fun execute(): ExecutionState {
        val expression = inputs.first().getValue() as String
        val result = MathInterpreter.executeExpression(expression)
        pinByName("result")?.setValue(result)
        return ExecutionState.COMPLETED
    }
}