package com.robotutor.nexora.feed.exceptions

import com.robotutor.nexora.webClient.exceptions.ServiceError


enum class NexoraError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0301("NEXORA-0301", "Feed not found."),
}