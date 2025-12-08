package com.robotutor.nexora.common.security.application.strategy

import com.robotutor.nexora.common.security.domain.vo.PrincipalContext
import com.robotutor.nexora.common.security.domain.vo.PrincipalData
import reactor.core.publisher.Mono

interface DataRetrieverStrategy<C : PrincipalContext, P : PrincipalData> {
    fun getPrincipalData(context: C): Mono<P>
}
