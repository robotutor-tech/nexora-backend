package com.robotutor.nexora.shared.domain.exception

import org.springframework.http.HttpStatus


open class BaseException(
    val errorCode: String,
    val status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
    override val message: String,
    var details: Map<String, Any> = emptyMap(),
    override val cause: Throwable? = null
) : Throwable(message = message, cause = cause) {
    constructor(
        serviceError: ServiceError,
        status: HttpStatus,
        details: Map<String, Any> = emptyMap(),
        cause: Throwable? = null
    ) : this(
        errorCode = serviceError.errorCode,
        status = status,
        message = serviceError.message,
        details = details,
        cause = cause
    )

    fun errorResponse(): ErrorResponse {
        return ErrorResponse(errorCode = errorCode, message = message)
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "status" to status.value(),
            "errorCode" to errorCode,
            "message" to message,
            "details" to details
        )
    }
}

data class ErrorResponse(override val errorCode: String, override val message: String) : ServiceError
