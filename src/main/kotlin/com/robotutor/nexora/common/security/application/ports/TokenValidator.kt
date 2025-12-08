package com.robotutor.nexora.common.security.application.ports

import com.robotutor.nexora.common.security.domain.vo.TokenValidationResult
import reactor.core.publisher.Mono

interface TokenValidator {
    fun validate(token: String): Mono<TokenValidationResult>
}