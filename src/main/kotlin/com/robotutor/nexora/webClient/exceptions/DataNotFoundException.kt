package com.robotutor.nexora.webClient.exceptions

class DataNotFoundException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
