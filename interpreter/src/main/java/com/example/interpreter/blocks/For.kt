package com.example.interpreter.blocks

import com.example.interpreter.models.ExecutionState
import com.example.interpreter.models.Id
import com.example.interpreter.models.ScopeBlock
import com.example.interpreter.models.PinManager

class ForBlock(
    id: Id
) : ScopeBlock(
    id,
    "For",
    mutableListOf(
        PinManager.createPinInt("first", ownId = id),
        PinManager.createPinInt("last", ownId = id),
        PinManager.createPinInt("step", ownId = id, value = 1)
    ),
    mutableListOf(
        PinManager.createPinBlock("body", ownId = id),
        PinManager.createPinInt("index", ownId = id),
        PinManager.createPinBlock("completed", ownId = id))
) {
    private var currentIndex: Int? = null
    private var isFirstExecution = true

    override fun execute(): ExecutionState {
        val first = pinByName("first")?.getValue() as? Int ?: throw Exception("First must be an int")
        val last = pinByName("last")?.getValue() as? Int ?: throw Exception("Last must be an int")
        val step = pinByName("step")?.getValue() as? Int ?: throw Exception("Step must be an int")

        currentIndex = (currentIndex ?: first)

        if (isFirstExecution && first <= last) {
            currentIndex = first
            pinByName("index")?.setValue(currentIndex)
            pinByName("completed")?.disable()
            isFirstExecution = false
            return ExecutionState.RUNNING
        }

        // Вроде как можем утверждать что оно точно не null.
        if (step > 0) {
            if (currentIndex!! + step <= last) {
                currentIndex = (currentIndex ?: first) + step
                pinByName("index")?.setValue(currentIndex)
                pinByName("completed")?.disable()
                return ExecutionState.RUNNING
            }
        } else {
            if (currentIndex!! > last) {
                currentIndex = (currentIndex ?: first) + step
                pinByName("index")?.setValue(currentIndex)
                pinByName("completed")?.disable()
                return ExecutionState.RUNNING
            }
        }

        // Это чтобы можно было вызывать несколько раз выполнение этого блока.
        // Для двойных циклов, например.
        currentIndex = first
        pinByName("completed")?.enable()
        pinByName("index")?.disable()
        pinByName("body")?.disable()
        isFirstExecution = true

        return ExecutionState.COMPLETED
    }

    override fun rollback() {
        outputs.forEach { pin -> pin.enable() }
    }
}