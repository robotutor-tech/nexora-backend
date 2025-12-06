package com.robotutor.nexora.context.user.domain.exception

import com.robotutor.nexora.shared.domain.exception.ServiceError

enum class NexoraError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0201("NEXORA-0201", "User registration denied"),
    NEXORA0202("NEXORA-0202", "Non registered user must have accountId."),
    NEXORA0203("NEXORA-0203", "Registered user must not have accountId."),
    NEXORA0204("NEXORA-0204", "User is not in registered state."),
}