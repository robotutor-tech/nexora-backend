package com.robotutor.nexora.webClient.exceptions

class AccessDeniedException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
