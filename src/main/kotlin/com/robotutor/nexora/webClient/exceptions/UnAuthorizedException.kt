package com.robotutor.nexora.webClient.exceptions

class UnAuthorizedException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
