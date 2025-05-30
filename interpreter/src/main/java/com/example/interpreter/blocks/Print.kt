package com.example.interpreter.blocks

import com.example.interpreter.models.Block
import com.example.interpreter.models.ExecutionState
import com.example.interpreter.models.Id
import com.example.interpreter.models.PinManager
import java.io.Writer

class PrintBlock internal constructor(
    id: Id,
    val writer: Writer
) : Block(
    id,
    "Print",
    mutableListOf(PinManager.createPinAny("a", ownId = id)),
    mutableListOf()
) {
    override fun execute(): ExecutionState {

        inputs.forEach { input ->
            writer.write("${input.getStringValue()}\n")
            writer.flush()
        }
        return ExecutionState.COMPLETED
    }
}