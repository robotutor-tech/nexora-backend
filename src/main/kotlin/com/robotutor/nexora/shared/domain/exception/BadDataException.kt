package com.robotutor.nexora.shared.domain.exception

class BadDataException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
