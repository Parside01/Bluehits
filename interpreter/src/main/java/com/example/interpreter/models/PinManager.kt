package com.example.interpreter.models

import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

object PinManager {
    private val pinRegistry = mutableMapOf<String, Pin>()
    private val idCounter = AtomicInteger(0)

    private fun generateId(): Id {
        return Id("pin-${idCounter.getAndIncrement()}")
    }

    fun getPin(id: Id) = pinRegistry[id.string()]

    fun <T : Pin> createPinInternal(createPinFunc: (Id) -> T): T {
        val id = generateId()
        val pin = createPinFunc(id)
        pinRegistry[id.string()] = pin
        return pin
    }

    fun setPinValue(id: Id, value: Any) {
        getPin(id)?.setValue(value)
    }

    internal fun <T : Any> createPin(
        name: String,
        type: KClass<T>,
        value: T = Utils.getDefaultValue(type.java),
        ownId: Id = Utils.getDefaultValue(Id::class.java),
        elementType: KClass<*>? = null
    ): TPin<T> {
        return createPinInternal { id ->
            TPin(id, ownId, name, Utils.getDefaultValue(type.java), value, elementType = elementType)
        }
    }

    fun createPinBlock(name: String, ownId: Id = Utils.getDefaultValue(Id::class.java)): TPin<Id> {
        return createPinInternal { id -> TPin(id, ownId, name, Utils.getDefaultValue(Id::class.java), id) }
    }

    fun createPinInt(name: String, value: Int = Utils.getDefaultValue(Int::class.java), ownId: Id = Utils.getDefaultValue(Id::class.java)): TPin<Int> {
        return createPinInternal { id -> TPin(id, ownId,  name, Utils.getDefaultValue(Int::class.java), value) }
    }

    fun createPinAny(name: String, value: Any = Any(), ownId: Id = Utils.getDefaultValue(Id::class.java)): TPin<Any> {
        return createPinInternal { id -> TPin(id, ownId, name, Any(), value) }
    }

    fun <T : Any> createPinArray(
        name: String,
        value: List<T> = emptyList(),
        ownId: Id = Utils.getDefaultValue(Id::class.java),
        elementType: KClass<T>
    ): TPin<List<T>> {
        return createPinInternal { id -> TPin(id, ownId, name, emptyList(), value, elementType = elementType) }
    }

    fun createPinBool(name: String, value: Boolean = false, ownId: Id = Utils.getDefaultValue(Id::class.java)): TPin<Boolean> {
        return createPinInternal { id -> TPin(id, ownId, name, false, value) }
    }

    fun createPinFloat(name: String, value: Float = Utils.getDefaultValue(Float::class.java),ownId: Id = Utils.getDefaultValue(Id::class.java)) :TPin<Float> {
        return createPinInternal { id -> TPin(id, ownId, name, Utils.getDefaultValue(Float::class.java), value) }
    }

    fun createPinString(name: String, value: String = Utils.getDefaultValue(String::class.java), ownId: Id = Utils.getDefaultValue(Id::class.java)): TPin<String> {
        return createPinInternal { id -> TPin(id, ownId, name, Utils.getDefaultValue(String::class.java), value) }
    }

    // Черная магия.
    fun copyPin(pinToCopy: Pin, ownId: Id): Pin {
        val originalName = pinToCopy.name
        val originalOwnId = pinToCopy.ownId
        val originalValue = pinToCopy.getValue()
        val originalKClass = pinToCopy.getType()
        val originalElementType = pinToCopy.getElementType()

        @Suppress("UNCHECKED_CAST")
        fun <S : Any> createTypedCopy(
            name: String,
            kClass: KClass<S>,
            value: Any?,
            ownId: Id,
            elementType: KClass<*>
        ): TPin<S> {
            return if (value != null) {
                createPin(name = name, type = kClass, value = value as S, ownId = ownId, elementType=elementType)
            } else {
                createPin(name = name, type = kClass, ownId = ownId, elementType = elementType)
            }
        }

        return createTypedCopy(
            name = originalName,
            kClass = originalKClass as KClass<out Any>,
            value = originalValue,
            ownId = ownId,
            elementType = originalElementType
        )
    }
}