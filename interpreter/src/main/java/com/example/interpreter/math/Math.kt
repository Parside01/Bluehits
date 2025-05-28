package com.example.interpreter.math

import java.util.Stack

object MathInterpreter {
    private val precedence = mapOf(
        '+' to 1,
        '-' to 1,
        '*' to 2,
        '/' to 2,
        '^' to 3,
        '√' to 4,
    )

    fun parse(expression: String): Expression {
        val expressionTokens = infixToRPN(expression)
        return buildExpressionTree(expressionTokens)
    }

    private fun infixToRPN(expression: String): List<String> {
        val output = mutableListOf<String>()
        val operators = Stack<Char>()
        var i = 0

        while (i < expression.length) {
            val c = expression[i]

            when {
                c.isWhitespace() -> i++
                c.isDigit() || c == '.' -> {
                    val num = StringBuilder()
                    while (i < expression.length && (expression[i].isDigit() || expression[i] == '.')) {
                        num.append(expression[i++])
                    }
                    output.add(num.toString())
                }
                c == '(' -> {
                    operators.push(c)
                    i++
                }
                c == ')' -> {
                    while (operators.peek() != '(') {
                        output.add(operators.pop().toString())
                    }
                    operators.pop()
                    i++
                }
                else -> {
                    val isUnaryMinus = (c == '-') && (i == 0 || expression[i-1] == '(' ||
                            precedence.containsKey(expression[i-1]))
                    val currentOp = if (isUnaryMinus) 'u' else c

                    while (!operators.empty() && operators.peek() != '(' &&
                        (precedence[operators.peek()] ?: 0) >= (precedence[currentOp] ?: 0)) {
                        output.add(operators.pop().toString())
                    }
                    operators.push(currentOp)
                    i++
                }
            }
        }

        while (!operators.empty()) {
            output.add(operators.pop().toString())
        }

        return output
    }

    private fun buildExpressionTree(tokens: List<String>): Expression {
        val stack = Stack<Expression>()

        for (token in tokens) {
            when (token) {
                "+" -> {
                    val right = stack.pop()
                    val left = stack.pop()
                    stack.push(AddExpression(left, right))
                }
                "-" -> {
                    val right = stack.pop()
                    val left = stack.pop()
                    stack.push(SubExpression(left, right))
                }
                "*" -> {
                    val right = stack.pop()
                    val left = stack.pop()
                    stack.push(MultiplyExpression(left, right))
                }
                "/" -> {
                    val right = stack.pop()
                    val left = stack.pop()
                    stack.push(DivideExpression(left, right))
                }
                "^" -> {
                    val right = stack.pop()
                    val left = stack.pop()
                    stack.push(PowerExpression(left, right))
                }
                "√" -> {
                    val expr = stack.pop()
                    stack.push(SqrtExpression(expr))
                }
                else -> stack.push(NumberExpression(token.toDouble()))
            }
        }

        if (stack.size != 1) throw IllegalArgumentException("Invalid expression")
        return stack.pop()
    }

    fun executeExpression(expression: String): Double {
        val exprTree = parse(expression)
        return exprTree.execute()
    }
}