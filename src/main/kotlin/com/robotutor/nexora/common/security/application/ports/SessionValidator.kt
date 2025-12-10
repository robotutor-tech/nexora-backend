package com.robotutor.nexora.common.security.application.ports

import com.robotutor.nexora.common.security.domain.vo.SessionValidationResult
import reactor.core.publisher.Mono

interface SessionValidator {
    fun validate(token: String): Mono<SessionValidationResult>
}