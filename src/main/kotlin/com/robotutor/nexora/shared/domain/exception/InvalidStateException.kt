package com.robotutor.nexora.shared.domain.exception

import org.springframework.http.HttpStatus

class InvalidStateException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, HttpStatus.CONFLICT, details)
