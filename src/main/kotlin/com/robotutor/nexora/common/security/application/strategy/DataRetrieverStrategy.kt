package com.robotutor.nexora.common.security.application.strategy

import com.robotutor.nexora.shared.domain.model.PrincipalData
import reactor.core.publisher.Mono

interface DataRetrieverStrategy {
    fun getPrincipalData(principalId: String): Mono<PrincipalData>
}
