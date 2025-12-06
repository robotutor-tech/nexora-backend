package com.robotutor.nexora.shared.domain.exception

import org.springframework.http.HttpStatus

class BadDataException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, HttpStatus.BAD_REQUEST, details)
