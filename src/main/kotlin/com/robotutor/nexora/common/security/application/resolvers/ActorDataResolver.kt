package com.robotutor.nexora.common.security.application.resolvers

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.common.security.domain.exceptions.NexoraError
import com.robotutor.nexora.common.security.models.PremisesActorData
import com.robotutor.nexora.shared.adapters.webclient.exceptions.UnAuthorizedException
import com.robotutor.nexora.shared.domain.model.ActorData
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class ActorDataResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == ActorData::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> {
        return Mono.deferContextual { context ->
            val actorData = context.getOrEmpty<PremisesActorData>(ActorData::class.java)
            if (actorData.isPresent) {
                createMono(actorData.get())
            } else {
                createMonoError(UnAuthorizedException(NexoraError.NEXORA0103))
            }
        }
    }
}
