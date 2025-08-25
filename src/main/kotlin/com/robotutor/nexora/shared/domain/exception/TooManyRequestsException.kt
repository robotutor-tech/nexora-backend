package com.robotutor.nexora.shared.domain.exception

class TooManyRequestsException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
