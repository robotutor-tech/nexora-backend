package com.robotutor.nexora.module.premises.domain.repository

import com.robotutor.nexora.shared.domain.vo.PremisesId
import reactor.core.publisher.Mono

interface PremisesIdGenerator {
    fun generate(): Mono<PremisesId>
}