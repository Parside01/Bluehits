package com.example.interpreter.models

class Connection internal constructor(
    from: Pin,
    to: Pin,
    val id: Id = Id("null")
) {
    private var from: Pin
    private var to: Pin
    private var isExecuted: Boolean = false

    init {
        if (from == to) throw IllegalArgumentException("Cannot connect the same pin.")
        if (from.id == to.id) throw IllegalArgumentException("Cannot connect pins with the same ID.")
        if ((from.type == PinType.BLOCK) xor (to.type == PinType.BLOCK)) throw IllegalArgumentException("Cannot connect pins of different types. (Blocks)")
        if (to.type != PinType.ANY && from.type != to.type) throw IllegalArgumentException("Cannot connect pins of different types. (Other types)")
        if (to.ownId == from.ownId) throw IllegalArgumentException("Cannot connect pins with the same ownId.")
        this.from = from
        this.to = to
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