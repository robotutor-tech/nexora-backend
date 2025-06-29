package com.robotutor.nexora.saga.exceptions

import com.robotutor.nexora.webClient.exceptions.ServiceError

enum class NexoraError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0201("NEXORA-0201", "Saga not found")
}