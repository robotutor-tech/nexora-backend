package com.robotutor.nexora.common.resolver

import com.robotutor.nexora.shared.application.ReactiveContext
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.lang.reflect.Parameter

@Component
class ActorDataResolver : ArgumentResolver {
    override fun supportsParameter(parameter: Parameter): Boolean {
        return parameter.type == ActorData::class.java
    }

    override fun resolveArgument(): Mono<Any> {
        @Suppress("UNCHECKED_CAST")
        return ReactiveContext.getActorData() as Mono<Any>
    }
}
