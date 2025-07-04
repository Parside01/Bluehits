package com.example.interpreter.models

import kotlin.reflect.KClass

object Utils {
    @Suppress("UNCHECKED_CAST")
    fun <T> getDefaultValue(type: Class<T>): T {
        return when (type) {
            Int::class.java -> 0 as T
            Integer::class.java -> 0 as T
            Long::class.java -> 0L as T
            java.lang.Long::class.java -> 0L as T
            Short::class.java -> 0.toShort() as T
            java.lang.Short::class.java -> 0.toShort() as T
            Byte::class.java -> 0.toByte() as T
            java.lang.Byte::class.java -> 0.toByte() as T
            Float::class.java -> 0.0f as T
            java.lang.Float::class.java -> 0.0f as T
            Double::class.java -> 0.0 as T
            java.lang.Double::class.java -> 0.0 as T
            Boolean::class.java -> false as T
            java.lang.Boolean::class.java -> false as T
            Char::class.java -> '\u0000' as T
            Character::class.java -> '\u0000' as T
            Any::class.java -> Any() as T
            String::class.java -> "" as T
            Id::class.java -> Id("null") as T
            List::class.java -> emptyList<Any>() as T
            Number::class.java -> 0 as T
            emptyList<T>()::class.java -> emptyList<T>() as T
            else -> throw IllegalArgumentException("Unknown type $type for get default value")
        }
    }

    fun getKClassFromJavaType(javaType: java.lang.reflect.Type): KClass<out Any>? {
        return when (javaType) {
            Int::class.java -> Int::class
            Float::class.java -> Float::class
            Boolean::class.java -> Boolean::class
            String::class.java -> String::class
            Id::class.java -> Id::class
            List::class.java -> List::class
            else -> null
        }
    }

    fun anyToString(value :Any): String {
        return when (value) {
            is String -> value
            is Int -> value.toString()
            is Double -> value.toString()
            is Boolean -> value.toString()
            is Float -> value.toString()
            is List<*> -> "[${value.joinToString(", ")}]"
            else -> ""
        }
    }

}