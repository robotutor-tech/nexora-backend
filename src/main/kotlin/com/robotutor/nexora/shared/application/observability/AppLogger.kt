package com.robotutor.nexora.shared.application.observability

/**
 * Application-layer logging port.
 *
 * Bounded Context application services/use-cases should depend on this interface,
 * not on infrastructure loggers from `common`.
 */
interface AppLogger {
    fun info(
        message: String,
        additionalDetails: Map<String, Any?> = emptyMap(),
        searchableFields: Map<String, Any?> = emptyMap(),
        skipAdditionalDetails: Boolean = false,
        skipResponseBody: Boolean = true,
    )

    fun error(
        errorMessage: String,
        throwable: Throwable?,
        errorCode: String? = null,
        additionalDetails: Map<String, Any?> = emptyMap(),
        searchableFields: Map<String, Any?> = emptyMap(),
        skipAdditionalDetails: Boolean = false,
    )
}

interface AppLoggerFactory {
    fun forClass(clazz: Class<*>): AppLogger
}

