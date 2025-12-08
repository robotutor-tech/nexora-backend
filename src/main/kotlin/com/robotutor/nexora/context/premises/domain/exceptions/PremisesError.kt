package com.robotutor.nexora.context.premises.domain.exceptions

import com.robotutor.nexora.shared.domain.exception.ServiceError

enum class PremisesError(override val errorCode: String, override val message: String): ServiceError {
    NEXORA0501("NEXORA-0501", "Premises registration denied")
}