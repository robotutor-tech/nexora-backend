package com.robotutor.nexora.shared.domain.exception

enum class SharedNexoraError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0101("NEXORA-0101", "User is not authenticated"),
    NEXORA0102("NEXORA-0102", "Account data not found"),
    NEXORA0103("NEXORA-0103", "Event message not found"),
    NEXORA0104("NEXORA-0104", "Event message context not found"),
    NEXORA0105("NEXORA-0105", "User data not found"),
    NEXORA0106("NEXORA-0106", "Actor data not found"),
    NEXORA0107("NEXORA-0107", "Device data not found"),
}
