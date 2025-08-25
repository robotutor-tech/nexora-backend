package com.robotutor.nexora.shared.domain.exception

class ServerException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
