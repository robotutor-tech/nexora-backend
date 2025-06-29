package com.robotutor.nexora.webClient.exceptions

class ServerException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
