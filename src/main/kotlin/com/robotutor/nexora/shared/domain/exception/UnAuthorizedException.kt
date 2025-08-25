package com.robotutor.nexora.shared.domain.exception

class UnAuthorizedException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
