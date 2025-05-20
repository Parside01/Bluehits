package com.example.interpreter.models

import kotlin.reflect.KClass

class Connection internal constructor(
    from: Pin,
    to: Pin,
    val id: Id = Id("null")
) {
    private var from: Pin
    private var to: Pin
    private var isExecuted: Boolean = false

//    init {
//        if (from == to) throw IllegalArgumentException("Cannot connect the same pin.")
//        if (from.id == to.id) throw IllegalArgumentException("Cannot connect pins with the same ID.")
//        if ((from.type == PinType.BLOCK) xor (to.type == PinType.BLOCK)) throw IllegalArgumentException("Cannot connect pins of different types. (Blocks)")
//
////        TODO: Щас можно ставить Any -> Int что как бы такое себе, надо что-то придумывать
//        if (to.type != PinType.ANY && from.type != PinType.ANY && from.type != to.type) throw IllegalArgumentException("Cannot connect pins of different types. (Other types)")
//        if (to.ownId == from.ownId) throw IllegalArgumentException("Cannot connect pins with the same ownId.")
//
//        this.from = from
//        this.to = to
//    }

    init {
        if (from == to) throw IllegalArgumentException("Cannot connect the same pin.")
        if (from.id == to.id) throw IllegalArgumentException("Cannot connect pins with the same ID.")
        if (isBlock(from.getType()) xor isBlock(to.getType())) throw IllegalArgumentException("Cannot connect pins of different types. (Blocks)")
        println("to ${to.getType()} from ${from.getType()}")
        if (!isAny(to.getType()) && !isAny(from.getType()) && from.getType() != to.getType()) throw IllegalArgumentException("Cannot connect pins of different types. (Other types)")
        if (to.ownId == from.ownId) throw IllegalArgumentException("Cannot connect pins with the same ownId.")

        this.from = from
        this.to = to
    }

    private fun isBlock(type: KClass<*>): Boolean {
        return type == Id::class
    }

    private fun isAny(type: KClass<*>): Boolean {
        return type == Any::class
    }


    fun executed() = isExecuted

    fun getFrom(): Pin {
        return from
    }

    fun getTo(): Pin {
        return to
    }

    // Как бы удаляем коннект. Деструторов нет, сорямба😶‍🌫️
    // Делаем именно для to, так как из from может идти несколько связей,
    // а в to обязательно одна.
    fun destroy() {
        this.to.reset()
    }

    fun execute() {
        this.to.setValue(this.from.getValue())
        isExecuted = true
    }
}