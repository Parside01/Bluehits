package interpreter.models

abstract class Block internal constructor (
    val id: BlockId,
    val name: String?,
    val inputs: List<Pin> = emptyList(),
    val outputs: List<Pin> = emptyList()
) {
    abstract fun execute();
}

data class BlockId (
    private val id: String
) {
    fun string() = id
}