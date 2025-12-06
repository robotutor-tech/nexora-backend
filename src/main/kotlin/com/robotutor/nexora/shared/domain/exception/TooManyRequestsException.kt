package com.robotutor.nexora.shared.domain.exception

import org.springframework.http.HttpStatus

class TooManyRequestsException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, HttpStatus.TOO_MANY_REQUESTS, details)
