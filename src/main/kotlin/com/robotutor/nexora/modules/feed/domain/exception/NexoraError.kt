package com.robotutor.nexora.modules.feed.domain.exception

import com.robotutor.nexora.shared.domain.exception.ServiceError

enum class NexoraError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0301("NEXORA-0301", "Feed not found."),
}