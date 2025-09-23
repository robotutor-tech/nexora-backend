package com.robotutor.nexora.modules.iam.application.facade

import com.robotutor.nexora.modules.iam.domain.entity.PolicyInput
import reactor.core.publisher.Mono

interface OpaFacade {
    fun evaluate(input: PolicyInput): Mono<Boolean>
}