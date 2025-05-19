package com.example.interpreter.models

import kotlin.reflect.KClass

internal object VariableManager {
    private val variableRegistry = mutableMapOf<String, Pair<KClass<*>, VarState<*>>>()

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getOrCreateVarState(name: String, defaultValue: T, type: KClass<T>): VarState<T> {
        val existingEntry = variableRegistry[name]

        if (existingEntry != null) {
            val (storedType, storedState) = existingEntry
            if (storedType != type) {
                throw IllegalArgumentException(
                    "Variable name '$name' is already used with a different type: ${storedType.simpleName}. Requested type: ${type.simpleName}"
                )
            }
            return storedState as VarState<T>
        }
        val newState = VarState(defaultValue)
        variableRegistry[name] = Pair(type, newState)
        return newState
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getVariableState(name: String, type: KClass<T>): VarState<T>? {
        val existingEntry = variableRegistry[name] ?: return null
        val (storedType, storedState) = existingEntry
        if (storedType != type) {
            throw IllegalArgumentException(
                "Variable name '$name' exists but has a different type: ${storedType.simpleName}. Requested type: ${type.simpleName}"
            )
        }
        return storedState as VarState<T>
    }

    fun clearAllVariables() {
        variableRegistry.clear()
    }
}