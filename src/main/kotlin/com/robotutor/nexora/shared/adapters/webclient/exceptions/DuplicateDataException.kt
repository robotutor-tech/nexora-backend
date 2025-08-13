package com.robotutor.nexora.shared.adapters.webclient.exceptions

class DuplicateDataException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
