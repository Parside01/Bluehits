package com.example.interpreter.models

import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.set

object ConnectionManager {
    private val connectionRegistry = mutableMapOf<String, Connection>()
    private val idCounter = AtomicInteger(0)

    private fun generateId(): Id {
        return Id("conn-${idCounter.getAndIncrement()}")
    }

    // TODO: Надо как-то оптимизированее это делать, явно.
    fun getPinConnections(pin: Pin): List<Connection> {
        return connectionRegistry.values.filter {
            (!it.getFrom().isDisabled() && !it.getTo().isDisabled())
                    && ((it.getFrom().id == pin.id) || (it.getTo().id == pin.id))
        }
    }

    fun getConnection(id: String) = connectionRegistry[id]

    fun executeConnection(id: String) {
        connectionRegistry[id]?.execute()
    }

    fun connect(from: Pin, to: Pin): Connection {
        try {
            val id = generateId()
            val connection = Connection(from, to, id)
            connectionRegistry[id.string()] = connection
            return connection
        } catch (e: Exception) {
            throw e
        }
    }

    fun disconnect(id: String) {
        val connection = connectionRegistry[id]
        connection?.destroy()
        connectionRegistry.remove(id)
    }

    private fun createConnection(createConnFunc: (Id) -> Connection): Connection {
        val id = generateId()
        val conn = createConnFunc(id)
        connectionRegistry[id.string()] = conn
        return conn
    }

    internal fun rollback() {
        connectionRegistry.values.forEach { it.rollback() }
    }
}