package com.robotutor.nexora.webClient.exceptions

class TooManyRequestsException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
