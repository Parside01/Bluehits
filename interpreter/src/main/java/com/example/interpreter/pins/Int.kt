//package com.example.interpreter.pins
//
//import com.example.interpreter.models.Id
//import com.example.interpreter.models.Pin
//import com.example.interpreter.models.PinType
//
//internal class PinInt internal constructor(
//    id: Id,
//    ownId: Id,
//    name: String,
//    private var value: Int = 0
//) : Pin(id, ownId, name, PinType.INT) {
//    override var zeroValue: Any = 0
//
//    init {
//        isSet = value != zeroValue
//    }
//
//    override fun getValue(): Any {
//        if (!isSet) return zeroValue
//        return value
//    }
//
//    override fun setValue(value: Any) {
//        if (value is Int) {
//            this.value = value
//            this.isSet = true
//        } else {
//            throw IllegalArgumentException("Value must be Int")
//        }
//    }
//}