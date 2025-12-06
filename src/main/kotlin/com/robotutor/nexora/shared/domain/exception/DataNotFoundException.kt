package com.robotutor.nexora.shared.domain.exception

import org.springframework.http.HttpStatus

class DataNotFoundException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, HttpStatus.NOT_FOUND, details)
