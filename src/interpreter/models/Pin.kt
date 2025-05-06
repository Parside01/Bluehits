package interpreter.models


// TODO: Может надо на жавовский Type переписать.
abstract class Pin internal constructor(
    val id: Id,
    val ownId: Id,
    val name: String,
    val type: PinType,
    protected var isSet: Boolean = false,
) {
    abstract var zeroValue: Any

    abstract fun getValue(): Any
    abstract fun setValue(value: Any)

    fun isPinSet() = isSet

    fun reset() {
        this.isSet = false
    }
}

enum class PinType {
    INT,
    BOOL,
    ANY,
    BLOCK
}
