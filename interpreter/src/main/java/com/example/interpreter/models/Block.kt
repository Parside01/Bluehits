package com.example.interpreter.models

abstract class Block internal constructor(
    val id: Id,
    val name: String?,
    val inputs: MutableList<Pin> = mutableListOf(),
    val outputs: MutableList<Pin> = mutableListOf()
) {
    val blockPin: TPin<Id> = PinManager.createPinBlock("", ownId = id)
    val outBlockPin: TPin<Id> = PinManager.createPinBlock("", ownId = id)
    abstract fun execute(): ExecutionState;

    // Чтобы не было строй привязки к индексам. Хз посмотрим + или -
    fun pinByName(name: String): Pin? {
        return inputs.find { it.name == name } ?: outputs.find { it.name == name }
    }
}

abstract class ScopeBlock internal constructor(
    id: Id,
    name: String?,
    inputs: MutableList<Pin> = mutableListOf(),
    outputs: MutableList<Pin> = mutableListOf()
) : Block(id, name, inputs, outputs) {}


enum class ExecutionState {
    RUNNING, // Означает, что нужно запустить блок еще раз.
    COMPLETED,
}