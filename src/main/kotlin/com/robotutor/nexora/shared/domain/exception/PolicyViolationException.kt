package com.robotutor.nexora.shared.domain.exception

import org.springframework.http.HttpStatus

class PolicyViolationException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, HttpStatus.UNPROCESSABLE_ENTITY, details)