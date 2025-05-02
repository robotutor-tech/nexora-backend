package com.robotutor.nexora.iam.exceptions

import com.robotutor.nexora.webClient.exceptions.ServiceError


enum class NexoraError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0201("NEXORA-0201", "Actor not found."),
}