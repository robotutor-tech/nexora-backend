package com.robotutor.nexora.user.exceptions

import com.robotutor.nexora.webClient.exceptions.ServiceError

enum class NexoraError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0201("NEXORA-0201", "User already registered with this email.")
}