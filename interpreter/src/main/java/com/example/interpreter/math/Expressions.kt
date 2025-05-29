package com.example.interpreter.math

import kotlin.math.pow
import kotlin.math.sqrt


interface Expression {
    fun execute(): Double
}

class NumberExpression(private val number: Double) : Expression {
    override fun execute() = number
}

class AddExpression(private val left: Expression, private val right: Expression) : Expression {
    override fun execute() = left.execute() + right.execute()
}

class SubExpression(private val left: Expression, private val right: Expression) : Expression {
    override fun execute() = left.execute() - right.execute()
}

class MultiplyExpression(private val left: Expression, private val right: Expression) : Expression {
    override fun execute() = left.execute() * right.execute()
}

class DivideExpression(private val left: Expression, private val right: Expression) : Expression {
    override fun execute(): Double {
        val divisor = right.execute()
        if (divisor == 0.0) throw ArithmeticException("Division by zero")
        return left.execute() / divisor
    }
}

class ModExpression(private val left: Expression, private val right: Expression) : Expression {
    override fun execute() : Double {
        val divisor = right.execute()
        if (divisor == 0.0) throw ArithmeticException("Division by zero")
        return left.execute() % divisor
    }
}

class PowerExpression(private val left: Expression, private val right: Expression) : Expression {
    override fun execute() = left.execute().pow(right.execute())
}

class SqrtExpression(private val expr: Expression) : Expression {
    override fun execute(): Double {
        val value = expr.execute()
        if (value < 0) throw ArithmeticException("Square root of negative number")
        return sqrt(value)
    }
}