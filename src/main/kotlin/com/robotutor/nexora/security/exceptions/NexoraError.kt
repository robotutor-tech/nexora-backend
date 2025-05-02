package com.robotutor.nexora.security.exceptions

import com.robotutor.nexora.webClient.exceptions.ServiceError

enum class NexoraError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0101("NEXORA-0101", "UnAuthorized exception"),
    NEXORA0102("NEXORA-0102", "Unable to resolve user data"),
    NEXORA0103("NEXORA-0103", "Unable to resolve premises actor data"),
    NEXORA0104("NEXORA-0104", "Unable to resolve invitation data"),
}