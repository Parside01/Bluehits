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
    initValue: T? = null
) {
    private var value: T? = initValue
    var isSet: Boolean = initValue != null

    fun getValue(): Any {
        return value ?: zeroValue as Any
    }

    fun setValue(value: Any?) {
        @Suppress("UNCHECKED_CAST")
        this.value = value as T?
    }

    // Молимся чтобы ничего не крашнулось.
    fun getType(): KClass<*> {
        return (value ?: zeroValue)!!::class
    }

    fun isPinSet() = isSet

    fun reset() {
        isSet = false
    }

    override fun toString() = "Pin(id=$id, ownId=$ownId, name=$name, value=$value)"
}