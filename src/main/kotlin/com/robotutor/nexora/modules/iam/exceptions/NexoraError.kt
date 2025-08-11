package com.robotutor.nexora.modules.iam.exceptions

import com.robotutor.nexora.shared.adapters.outbound.webclient.exceptions.ServiceError


enum class NexoraError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0201("NEXORA-0201", "Actor not found."),
    NEXORA0202("NEXORA-0202", "Policy creation request is invalid."),
}