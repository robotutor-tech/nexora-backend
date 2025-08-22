package com.robotutor.nexora.common.security.application.ports

import com.robotutor.nexora.common.security.domain.model.PolicyInput
import reactor.core.publisher.Mono

interface OpaFacade {
    fun evaluate(input: PolicyInput): Mono<Boolean>
}