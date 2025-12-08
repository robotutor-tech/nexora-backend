package com.robotutor.nexora.context.premises.domain.repository

import com.robotutor.nexora.shared.domain.model.PremisesId
import reactor.core.publisher.Mono

interface PremisesIdGenerator {
    fun generate(): Mono<PremisesId>
}