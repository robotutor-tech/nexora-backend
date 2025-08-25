package com.robotutor.nexora.shared.domain.exception

class DuplicateDataException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
