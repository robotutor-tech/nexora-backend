package com.robotutor.nexora.security.filters.resolvers

import com.robotutor.iot.exceptions.UnAuthorizedException
import com.robotutor.nexora.security.exceptions.NexoraError
import com.robotutor.nexora.security.createMono
import com.robotutor.nexora.security.createMonoError
import com.robotutor.nexora.security.models.UserData
import com.robotutor.nexora.security.models.UserPremisesData
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class UserPremisesDataResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == UserPremisesData::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> {
        return Mono.deferContextual { context ->
            val userData = context.getOrEmpty<UserData>(UserPremisesData::class.java)
            if (userData.isPresent) {
                createMono(userData.get())
            } else {
                createMonoError(UnAuthorizedException(NexoraError.NEXORA0102))
            }
        }
    }
}
