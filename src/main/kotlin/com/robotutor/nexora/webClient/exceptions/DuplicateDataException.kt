package com.robotutor.iot.exceptions

import com.robotutor.nexora.webClient.exceptions.BaseException
import com.robotutor.nexora.webClient.exceptions.ServiceError

class DuplicateDataException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
