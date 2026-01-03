package com.robotutor.nexora.shared.domain.exception

enum class SharedNexoraError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0101("NEXORA-0101", "Actor data not found"),
    NEXORA0102("NEXORA-0102", "Account data not found"),
    NEXORA0103("NEXORA-0103", "Event message not found"),
}