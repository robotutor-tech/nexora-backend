package com.robotutor.nexora.common.utils

class EvaluationContext {
    private val context = mutableMapOf<String, Any?>()

    fun put(key: String, value: Any): EvaluationContext {
        context[key] = value
        return this
    }

    fun get(key: String): Any? {
        return context[key]
    }
}