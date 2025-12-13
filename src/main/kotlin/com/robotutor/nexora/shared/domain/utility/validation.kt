package com.robotutor.nexora.shared.domain.utility

import com.robotutor.nexora.shared.domain.exception.BadDataException
import com.robotutor.nexora.shared.domain.exception.ErrorResponse

inline fun validation(value: Boolean, lazyError: () -> String) {
    if (!value) {
        throw BadDataException(ErrorResponse("NEXORA-0101", lazyError()))
    }
}