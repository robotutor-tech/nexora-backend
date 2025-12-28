package com.robotutor.nexora.common.security.domain.exception

import com.robotutor.nexora.shared.domain.exception.ServiceError

enum class SecurityError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0101("NEXORA-0101", "UnAuthorized exception"),
    NEXORA0105("NEXORA-0105", "Doesn't have permission for the resource"),
}