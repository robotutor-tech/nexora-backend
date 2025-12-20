package com.robotutor.nexora.context.iam.domain.exception

import com.robotutor.nexora.shared.domain.exception.ServiceError

enum class NexoraError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0201("NEXORA-0201", "Account registration denied"),
    NEXORA0202("NEXORA-0202", "Invalid credentials."),
    NEXORA0203("NEXORA-0203", "Account not found."),
    NEXORA0204("NEXORA-0204", "Premises resource creation denied"),
    NEXORA0205("NEXORA-0205", "Session token is invalid or expired."),
    NEXORA0206("NEXORA-0206", "Session is inactive or refresh limit exceeded."),
    NEXORA0207("NEXORA-0207", "Account is not in REGISTERED state."),
}