package test.model.model

import interpreter.models.Connection
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import interpreter.pins.PinBool
import interpreter.pins.PinInt

class ConnectionTest {
    @Test
    fun testConnectionWithSameTypes() {
        val pin1 = PinBool("1", "a", true)
        val pin2 = PinBool("2", "b", false)

        val connection = Connection(pin1, pin2)

        val result = assertDoesNotThrow {
            Connection(pin1, pin2)
        }
    }

    @Test
    fun testSetConnectionWithSamePin() {
        val pin = PinInt("3", "a", 10)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            Connection(pin, pin)
        }
    }

    @Test
    fun testSetConnectionDifferentTypes() {
        val intPin = PinInt("4", "intPin", 5)
        val boolPin = PinBool("5", "boolPin", true)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            Connection(intPin, boolPin)
        }
    }

    @Test
    fun testSetConnectionWithSameId() {
        val pin1 = PinInt("6", "a", 10)
        val pin2 = PinInt("6", "b", 20)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            Connection(pin1, pin2)
        }
    }
}