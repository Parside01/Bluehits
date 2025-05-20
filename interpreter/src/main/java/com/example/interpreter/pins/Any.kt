//package com.example.interpreter.pins
//
//import com.example.interpreter.models.Id
//import com.example.interpreter.models.Pin
//import com.example.interpreter.models.PinType
//
//internal class PinAny internal constructor(
//    id: Id,
//    ownId: Id,
//    name: String,
//    private var value: Any = "null"
//) : Pin(id, ownId, name, PinType.ANY) {
//    override var zeroValue: Any = "null"
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
//        this.value = value
//        this.isSet = true
//    }
//}