package model

abstract class Pin (
    val id: String,
    val name: String,
    val type: PinType,
) {
    abstract fun getValue(): Any
    abstract fun setValue(value: Any)
    fun getType(): PinType {
        return type
    }
}

enum class PinType {
    INT,
    BOOL
}
