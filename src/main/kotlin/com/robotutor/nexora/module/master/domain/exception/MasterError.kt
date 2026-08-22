package com.robotutor.nexora.module.master.domain.exception

import com.robotutor.nexora.shared.domain.exception.ServiceError


enum class MasterError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0401("NEXORA-0401", "Address not found for the pin code."),
}
