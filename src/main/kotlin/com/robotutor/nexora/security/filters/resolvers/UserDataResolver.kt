package com.robotutor.nexora.security.filters.resolvers

import com.robotutor.nexora.webClient.exceptions.UnAuthorizedException
import com.robotutor.nexora.security.exceptions.NexoraError
import com.robotutor.nexora.security.createMono
import com.robotutor.nexora.security.createMonoError
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import com.robotutor.nexora.security.models.AuthUserData
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class UserDataResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == AuthUserData::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> {
        return Mono.deferContextual { context ->
            val authUserData = context.getOrEmpty<AuthUserData>(AuthUserData::class.java)
            if (authUserData.isPresent) {
                createMono(authUserData.get())
            } else {
                createMonoError(UnAuthorizedException(NexoraError.NEXORA0102))
            }
        }
    }
}
