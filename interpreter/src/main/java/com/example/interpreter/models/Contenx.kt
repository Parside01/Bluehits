package com.example.interpreter.models

import interpreter.models.Id

class Context internal constructor (
    val startBlocks: MutableList<Id>, // Блоки, с которых начнется выполнения контекста.
) {

}