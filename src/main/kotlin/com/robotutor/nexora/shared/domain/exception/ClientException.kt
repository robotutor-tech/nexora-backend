package com.robotutor.nexora.shared.domain.exception

class ClientException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
