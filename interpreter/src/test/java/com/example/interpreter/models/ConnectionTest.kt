package interpreter.models

import interpreter.pins.PinBlockId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import interpreter.pins.PinBool
import interpreter.pins.PinInt

//class ConnectionTest {
//    @Test
//    fun testConnectionWithSameTypes() {
//        val pin1 = PinBool(Id("1"), "a", true)
//        val pin2 = PinBool(Id("2"), "b", false)
//
//        assertDoesNotThrow {
//            Connection(pin1, pin2)
//        }
//    }
//
//    @Test
//    fun testSetConnectionWithSamePin() {
//        val pin = PinInt(Id("3"), "a", 10)
//
//        assertThrows(IllegalArgumentException::class.java) {
//            Connection(pin, pin)
//        }
//    }
//
//    @Test
//    fun testSetConnectionDifferentTypes() {
//        val intPin = PinInt(Id("4"), "intPin", 5)
//        val boolPin = PinBool(Id("5"), "boolPin", true)
//
//        assertThrows(IllegalArgumentException::class.java) {
//            Connection(intPin, boolPin)
//        }
//    }
//
//    @Test
//    fun testSetConnectionWithSameId() {
//        val pin1 = PinInt(Id("6"), "a", 10)
//        val pin2 = PinInt(Id("6"), "b", 20)
//
//        assertThrows(IllegalArgumentException::class.java) {
//            Connection(pin1, pin2)
//        }
//    }
//
//    @Test
//    fun testSetConnectionWithBlockPins() {
//        val pin1 = PinBlockId(Id("1"), "a", Id("2"))
//        val pin2 = PinBlockId(Id("2"), "b", Id("3"))
//
//        assertDoesNotThrow {
//            Connection(pin1, pin2)
//        }
//    }
//
//    @Test // :)
//    fun testSetConnectionWithBlockPinsWithDifferentTypes() {
//        val pin1 = PinInt(Id("1"), "a", 3)
//        val pin2 = PinBlockId(Id("2"), "b", Id("3"))
//
//        assertThrows(IllegalArgumentException::class.java) {
//            Connection(pin1, pin2)
//        }
//    }
//}