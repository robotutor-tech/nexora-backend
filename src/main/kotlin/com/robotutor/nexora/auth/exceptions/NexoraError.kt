package com.robotutor.nexora.auth.exceptions

import com.robotutor.nexora.webClient.exceptions.ServiceError


enum class NexoraError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0201("NEXORA-0201", "User already exists with this user id"),
    NEXORA0202("NEXORA-0202", "Invalid credentials"),
    NEXORA0203("NEXORA-0203", "Invalid token"),
}