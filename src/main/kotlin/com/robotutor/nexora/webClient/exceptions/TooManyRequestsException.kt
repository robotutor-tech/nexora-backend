package com.robotutor.iot.exceptions

import com.robotutor.nexora.webClient.exceptions.BaseException
import com.robotutor.nexora.webClient.exceptions.ServiceError

class TooManyRequestsException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
