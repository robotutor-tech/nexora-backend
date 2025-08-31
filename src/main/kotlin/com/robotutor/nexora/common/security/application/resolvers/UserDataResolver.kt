package com.robotutor.nexora.common.security.application.resolvers

import com.robotutor.nexora.shared.application.service.ContextDataResolver
import com.robotutor.nexora.shared.domain.model.UserData
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class UserDataResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == UserData::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> {
        @Suppress("UNCHECKED_CAST")
        return ContextDataResolver.getUserData() as Mono<Any>
    }
}
