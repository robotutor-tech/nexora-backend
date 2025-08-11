package com.robotutor.nexora.shared.adapters.outbound.webclient.exceptions

class BadDataException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
