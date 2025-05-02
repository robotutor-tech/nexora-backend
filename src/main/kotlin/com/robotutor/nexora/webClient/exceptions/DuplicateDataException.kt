package com.robotutor.nexora.webClient.exceptions

class DuplicateDataException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
