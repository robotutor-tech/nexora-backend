package com.robotutor.nexora.shared.domain.exception

import org.springframework.http.HttpStatus

class UnAuthorizedException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, HttpStatus.UNAUTHORIZED, details)
