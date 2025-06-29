package com.robotutor.nexora.orchestration.exceptions

import com.robotutor.nexora.webClient.exceptions.ServiceError

enum class NexoraError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0301("NEXORA-0301", "Failed to register user"),
    NEXORA0302("NEXORA-0302", "Failed to register premises"),
}