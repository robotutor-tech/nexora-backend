package com.robotutor.nexora.common.security.application.resolvers

import com.robotutor.nexora.shared.application.resolver.ContextDataResolver
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
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
        @Suppress("UNCHECKED_CAST")
        return ContextDataResolver.getActorData() as Mono<Any>
    }
}
