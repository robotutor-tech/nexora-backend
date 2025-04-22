package com.robotutor.iot.exceptions

import com.robotutor.nexora.webClient.exceptions.BaseException
import com.robotutor.nexora.webClient.exceptions.ServiceError

class DataNotFoundException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
