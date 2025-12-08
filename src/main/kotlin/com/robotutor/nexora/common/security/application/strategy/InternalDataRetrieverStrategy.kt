package com.robotutor.nexora.common.security.application.strategy

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.common.security.domain.vo.InternalData
import com.robotutor.nexora.common.security.domain.vo.InternalPrincipalContext
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class InternalDataRetrieverStrategy() : DataRetrieverStrategy<InternalPrincipalContext, InternalData> {
    override fun getPrincipalData(context: InternalPrincipalContext): Mono<InternalData> {
        return createMono(InternalData(context.id))
    }
}