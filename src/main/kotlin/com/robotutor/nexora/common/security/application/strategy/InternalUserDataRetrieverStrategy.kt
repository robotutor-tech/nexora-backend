package com.robotutor.nexora.common.security.application.strategy

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.shared.domain.model.InternalData
import com.robotutor.nexora.shared.domain.model.PrincipalData
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class InternalUserDataRetrieverStrategy() : DataRetrieverStrategy {
    override fun getPrincipalData(principalId: String): Mono<PrincipalData> {
        return createMono(InternalData(principalId))
    }
}