package com.robotutor.nexora.common.security.application.strategy

import com.robotutor.nexora.shared.domain.model.PrincipalContext
import com.robotutor.nexora.shared.domain.model.PrincipalData
import reactor.core.publisher.Mono

interface DataRetrieverStrategy<C : PrincipalContext, P : PrincipalData> {
    fun getPrincipalData(context:  C): Mono<P>
}
