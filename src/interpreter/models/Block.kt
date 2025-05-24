package interpreter.models

abstract class Block internal constructor (
    val id: Id,
    val name: String?,
    val inputs: MutableList<Pin> = mutableListOf(),
    val outputs: MutableList<Pin> = mutableListOf()
) {
    val blockPin: Pin = PinManager.createPinBlock("block", ownId = id)
    abstract fun execute();
}