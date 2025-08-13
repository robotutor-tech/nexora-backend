package com.robotutor.nexora.shared.adapters.webclient.exceptions

class DataNotFoundException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
