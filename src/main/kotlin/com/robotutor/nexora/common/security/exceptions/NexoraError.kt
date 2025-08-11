package com.robotutor.nexora.common.security.exceptions

import com.robotutor.nexora.shared.adapters.outbound.webclient.exceptions.ServiceError

enum class NexoraError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0101("NEXORA-0101", "UnAuthorized exception"),
    NEXORA0102("NEXORA-0102", "Unable to resolve user data"),
    NEXORA0103("NEXORA-0103", "Unable to resolve premises actor data"),
    NEXORA0104("NEXORA-0104", "Unable to resolve invitation data"),
    NEXORA0105("NEXORA-0105", "Doesn't have permission for the resource"),
}