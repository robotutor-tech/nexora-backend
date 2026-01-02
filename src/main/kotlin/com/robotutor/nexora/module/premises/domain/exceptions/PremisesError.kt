package com.robotutor.nexora.module.premises.domain.exceptions

import com.robotutor.nexora.shared.domain.exception.ServiceError

enum class PremisesError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0501("NEXORA-0501", "Premises registration denied"),
    NEXORA0502("NEXORA-0502", "Premises is not in REGISTERED state.")
}