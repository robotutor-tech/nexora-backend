package com.robotutor.nexora.webClient.exceptions

class BadDataException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
