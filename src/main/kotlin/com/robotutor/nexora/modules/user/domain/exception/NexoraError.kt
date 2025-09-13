package com.robotutor.nexora.modules.user.domain.exception

import com.robotutor.nexora.shared.domain.exception.ServiceError

enum class NexoraError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0201("NEXORA-0201", "User already registered with this email."),
    NEXORA0202("NEXORA-0202", "User not found.")
}