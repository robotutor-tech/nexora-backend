package com.robotutor.nexora.common.security.resolver

import com.robotutor.nexora.common.resolver.ActorDataResolver
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class HandlerActorDataResolver(private val actorDataResolver: ActorDataResolver) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return actorDataResolver.supportsParameter(parameter.parameter)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<in Any> {
        return actorDataResolver.resolveArgument()
    }
}