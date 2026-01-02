package com.robotutor.nexora.shared.domain.policy

class PolicyResult(
    private val allowed: Boolean,
    private val reasons: List<String> = listOf(),
    private val metadata: Map<String, Any> = emptyMap()
) {

    companion object {
        fun allow(metadata: Map<String, Any>? = null): PolicyResult {
            return PolicyResult(allowed = true, metadata = metadata ?: emptyMap())
        }

        fun deny(reasons: List<String>, metadata: Map<String, Any>? = null): PolicyResult {
            return PolicyResult(false, reasons, metadata ?: emptyMap())
        }

        fun create(reasons: List<String>): PolicyResult {
            return if (reasons.isEmpty()) allow() else deny(reasons)
        }
    }

    fun isAllowed(): Boolean {
        return allowed
    }

    fun isDenied(): Boolean {
        return !allowed
    }

    fun getReasons(): List<String> {
        return reasons.toList()
    }

    fun <T : Any> getMetadata(key: String): T? {
        @Suppress("UNCHECKED_CAST")
        return metadata[key] as T?
    }
}