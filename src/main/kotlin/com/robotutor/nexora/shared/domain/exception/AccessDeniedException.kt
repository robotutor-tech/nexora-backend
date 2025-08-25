package com.robotutor.nexora.shared.domain.exception

class AccessDeniedException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
