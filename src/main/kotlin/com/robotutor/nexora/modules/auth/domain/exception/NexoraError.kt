package com.robotutor.nexora.modules.auth.domain.exception

import com.robotutor.nexora.shared.domain.exception.ServiceError

enum class NexoraError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0201("NEXORA-0201", "User already exists with this user id"),
    NEXORA0202("NEXORA-0202", "Bad credentials"),
    NEXORA0203("NEXORA-0203", "UnAuthorized actor"),
    NEXORA0204("NEXORA-0204", "Invitation not found"),
    NEXORA0205("NEXORA-0205", "Invalid role for authorization"),
    NEXORA0206("NEXORA-0206", "Unauthorized user"),
}