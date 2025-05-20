package com.example.interpreter.models

import java.util.concurrent.atomic.AtomicInteger

object PinManager {
    private val pinRegistry = mutableMapOf<String, Pin<*>>()
    private val idCounter = AtomicInteger(0)

    private fun generateId(): Id {
        return Id("pin-${idCounter.getAndIncrement()}")
    }

    fun getPin(id: Id) = pinRegistry[id.string()]

    private fun <T : Pin<*>> createPin(createPinFunc: (Id) -> T): T {
        val id = generateId()
        val pin = createPinFunc(id)
        pinRegistry[id.string()] = pin
        return pin
    }

    fun setPinValue(id: Id, value: Any) {
        getPin(id)?.setValue(value)
    }

//    fun<T> createCustomPin(name: String, zeroValue: T, value: T? = null, ownId: Id) :Pin<T> {
//        val id = generateId()
//        val pin = Pin(id, ownId, name, zeroValue, value)
//        pinRegistry[id.string()] = pin
//        return pin
//    }

    fun createPinBlock(name: String, ownId: Id = Id("null")): Pin<Id> {
        return createPin { id -> Pin(id, ownId, name, Id("null"), id) }
    }

    fun createPinInt(name: String, value: Int = 0, ownId: Id = Id("null")): Pin<Int> {
        return createPin { id -> Pin(id, ownId,  name, 0, value) }
    }

    fun createPinAny(name: String, value: Any = "null", ownId: Id = Id("null")): Pin<Any> {
        return createPin { id -> Pin(id, ownId, name, "null", value) }
    }

    fun createPinArray(name: String, value: List<Any> = emptyList(), ownId: Id = Id("null")): Pin<List<Any>> {
        return createPin { id -> Pin(id, ownId, name, emptyList(), value) }
    }

    fun createPinBool(name: String, value: Boolean = false, ownId: Id = Id("null")): Pin<Boolean> {
        return createPin { id -> Pin(id, ownId, name, false, value) }
    }
}