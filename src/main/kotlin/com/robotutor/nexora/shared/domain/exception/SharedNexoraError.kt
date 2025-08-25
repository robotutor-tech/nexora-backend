package com.robotutor.nexora.shared.domain.exception

enum class SharedNexoraError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0101("NEXORA-0101", "Actor data not found"),
    NEXORA0102("NEXORA-0102", "User data not found"),
    NEXORA0103("NEXORA-0103", "Invitation data not found"),
}