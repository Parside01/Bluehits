package model

abstract class Block (
    val id: String,
    val name: String?,
    val inputs: List<Pin> = emptyList(),
    val outputs: List<Pin> = emptyList()
) {
    abstract fun execute();
}