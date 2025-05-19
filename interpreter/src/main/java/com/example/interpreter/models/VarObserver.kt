package com.example.interpreter.models

interface VarObserver<T> {
    fun onValueChanged(newValue: T)
}

class VarState<T>(initValue: T) {
    private var value: T = initValue

    private val observers: MutableList<VarObserver<T>> = mutableListOf()

    fun addObserver(observer: VarObserver<T>) {
        observers.add(observer)
    }

    fun removeObserver(observer: VarObserver<T>) {
        observers.remove(observer)
    }

    fun setValue(newValue: T) {
        if (this.value == newValue) return
        this.value = newValue
        observers.forEach { it.onValueChanged(newValue) }
    }

    fun getValue(): T {
        return this.value
    }
}