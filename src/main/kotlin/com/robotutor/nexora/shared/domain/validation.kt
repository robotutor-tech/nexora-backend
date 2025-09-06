package com.robotutor.nexora.shared.domain

import com.robotutor.nexora.shared.domain.exception.BadDataException
import com.robotutor.nexora.shared.domain.exception.ErrorResponse


inline fun validation(value: Boolean, lazyError: () -> String): Unit {
    if (!value) {
        throw BadDataException(ErrorResponse("NEXORA-0102", lazyError()))
    }
}