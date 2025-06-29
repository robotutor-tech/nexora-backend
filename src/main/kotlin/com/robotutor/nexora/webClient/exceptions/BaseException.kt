package com.robotutor.nexora.webClient.exceptions


open class BaseException(
    private val errorCode: String,
    override val message: String,
    private var details: Map<String, Any> = emptyMap(),
    override val cause: Throwable? = null
) : Throwable(message = message, cause = cause) {
    constructor(serviceError: ServiceError, details: Map<String, Any> = emptyMap(), cause: Throwable? = null) : this(
        serviceError.errorCode,
        serviceError.message,
        details,
        cause
    )

    fun errorResponse(): ErrorResponse {
        return ErrorResponse(errorCode = errorCode, message = message)
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "errorCode" to errorCode,
            "message" to message,
            "details" to details
        )
    }
}

data class ErrorResponse(override val errorCode: String, override val message: String): ServiceError
