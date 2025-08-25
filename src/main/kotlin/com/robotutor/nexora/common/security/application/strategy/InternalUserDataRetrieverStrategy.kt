package com.robotutor.nexora.common.security.application.strategy

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.common.security.domain.model.ValidateTokenResult
import com.robotutor.nexora.shared.domain.model.InternalData
import com.robotutor.nexora.shared.domain.model.PrincipalContext
import com.robotutor.nexora.shared.domain.model.PrincipalData
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class InternalUserDataRetrieverStrategy() : DataRetrieverStrategy<PrincipalContext, InternalData> {
    override fun getPrincipalData(context: PrincipalContext): Mono<InternalData> {
        return createMono(InternalData("internal data"))
    }
}