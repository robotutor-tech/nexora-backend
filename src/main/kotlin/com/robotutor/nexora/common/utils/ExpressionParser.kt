package com.robotutor.nexora.common.utils

data class ExpressionParser(val expression: String) {
    private val rawExpressions = mutableMapOf<String, List<String>>()
    private var isParsed = false

    fun getValue(context: EvaluationContext): String {
        if (!isParsed) parse()
        val keys = rawExpressions.keys.toList()
        var finalResult = expression
        keys.forEach { key ->
            val expressions = rawExpressions[key]!!
            var value = context.get(expressions[0])
            if (expressions.size > 1) {
                expressions
                    .subList(1, expressions.size - 1)
                    .forEach { expression ->
                        value = value?.let { it.toMap()[expression] }
                    }
                value = value?.let { it.toMap()[expressions.last()] }
            }
            finalResult = finalResult.replace(key, value?.toString() ?: "null")
        }
        return finalResult
    }


    private fun parse() {
        if (!isParsed) {
            var text = expression
            while (text.contains("#{")) {
                val start = text.indexOf("#{")
                val end = text.indexOf("}")
                val key = text.substring(start, end + 1)
                rawExpressions[key] = text.substring(start + 2, end).split(".")
                text = text.substring(end)
            }
            isParsed = true
        }
    }
}