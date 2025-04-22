package com.robotutor.nexora.security.exceptions

import com.robotutor.nexora.webClient.exceptions.ServiceError

enum class NexoraError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0101("NEXORA-0101", "User not authorized"),
    NEXORA0102("NEXORA-0102", "Unable to resolve user data"),
}