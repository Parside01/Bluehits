package interpreter.models

abstract class Block internal constructor (
    val id: Id,
    val name: String?,
    val inputs: MutableList<Pin> = mutableListOf(),
    val outputs: MutableList<Pin> = mutableListOf()
) {
    val blockPin: Pin = PinManager.createPinBlock("block", ownId = id)
    abstract fun execute(): ExecutionState;

    // Чтобы не было строй привязки к индексам.
    fun pinByName(name: String): Pin? {
        return inputs.find { it.name == name } ?: outputs.find { it.name == name }
    }
}


enum class ExecutionState {
    RUNNING, // Означает, что нужно запустить блок еще раз.
    COMPLETED,
}