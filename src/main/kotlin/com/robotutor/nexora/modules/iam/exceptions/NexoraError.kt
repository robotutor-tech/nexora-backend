package com.robotutor.nexora.modules.iam.exceptions

import com.robotutor.nexora.shared.domain.exception.ServiceError


enum class NexoraError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0201("NEXORA-0201", "Unauthorized account."),
    NEXORA0202("NEXORA-0202", "Invalid credentials."),
    NEXORA0203("NEXORA-0203", "Account not found."),
    NEXORA0204("NEXORA-0204", "Premises resource creation denied"),
    NEXORA0205("NEXORA-0202", "Policy creation request is invalid."),
    NEXORA0206("NEXORA-0202", "Policy creation request is invalid."),
    NEXORA0207("NEXORA-0202", "Policy creation request is invalid."),
}