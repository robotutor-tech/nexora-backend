package com.robotutor.nexora.modules.device.domain.exception

import com.robotutor.nexora.shared.domain.exception.ServiceError


enum class NexoraError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0401("NEXORA-0401", "Device not found"),
}