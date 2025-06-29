package com.robotutor.nexora.webClient.exceptions

class ClientException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
