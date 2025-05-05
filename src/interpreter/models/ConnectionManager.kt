package interpreter.models

import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.set

object ConnectionManager {
    private val connectionRegistry = mutableMapOf<String, Connection>()
    private val idCounter = AtomicInteger(0)

    private fun generateId(): Id {
        return Id("conn-${idCounter.getAndIncrement()}")
    }

    fun getConnection(id: String) = connectionRegistry[id]

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
}