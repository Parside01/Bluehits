package interpreter.models

abstract class Block internal constructor (
    val id: String,
    val name: String?,
    val inputs: List<Pin> = emptyList(),
    val outputs: List<Pin> = emptyList()
) {
    abstract fun execute();
}