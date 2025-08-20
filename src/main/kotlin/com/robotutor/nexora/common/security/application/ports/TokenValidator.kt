package com.robotutor.nexora.common.security.application.ports

import com.robotutor.nexora.common.security.domain.model.ValidateTokenResult
import reactor.core.publisher.Mono

interface TokenValidator {
    fun validate(token: String): Mono<ValidateTokenResult>
}