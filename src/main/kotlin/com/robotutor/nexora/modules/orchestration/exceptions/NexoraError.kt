package com.robotutor.nexora.modules.orchestration.exceptions

import com.robotutor.nexora.shared.adapters.webclient.exceptions.ServiceError

enum class NexoraError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0301("NEXORA-0301", "Failed to register user"),
    NEXORA0302("NEXORA-0302", "Failed to register premises"),
}