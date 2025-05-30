package com.example.interpreter.models

import kotlin.reflect.KClass


// TODO: Может надо на жавовский KClass переписать.
//abstract class Pin internal constructor(
//    val id: Id,
//    val ownId: Id,
//    val name: String,
//    val type: PinType,
//    protected var isSet: Boolean = false,
//) {
//    abstract var zeroValue: Any
//
//    abstract fun getValue(): Any
//    abstract fun setValue(value: Any)
//
//    fun isPinSet() = isSet
//
//    fun reset() {
//        this.isSet = false
//    }
//}
//
//enum class PinType {
//    INT,
//    ARRAY,
//    BOOL,
//    ANY,
//    BLOCK
//}

typealias Pin = TPin<*>

class TPin<T>(
    val id: Id,
    val ownId: Id,
    val name: String,
    private val zeroValue: T,
    initValue: T? = null,
    private val elementType: KClass<*>? = null
) {
    private var value: T? = initValue
    private var type: KClass<*> = zeroValue!!::class
    var isSet: Boolean = initValue != null
    private var isDisabled: Boolean = false

    internal fun enable() {
        isDisabled = false
    }

    internal fun disable() {
        isDisabled = true
    }

    internal fun isDisabled(): Boolean = isDisabled

    fun getValue(): Any {
        return value ?: zeroValue as Any
    }

    fun getStringValue(): String {
        return Utils.anyToString(getValue())
    }

    fun setValue(value: Any?) {
        @Suppress("UNCHECKED_CAST")
        this.value = value as T?
    }

    // Молимся чтобы ничего не крашнулось.
    fun getType(): KClass<*> {
        if (elementType != null) {
            return emptyList<T>()::class
        }
        return type
    }

    fun isPinSet() = isSet

    fun reset() {
        isSet = false
    }

    override fun toString() = "Pin(id=$id, ownId=$ownId, name=$name, value=$value)"
}