package com.robotutor.nexora.common.security.resolver

import com.robotutor.nexora.common.resolver.ArgumentResolver
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class HandlerResolver {
    fun from(argumentResolver: ArgumentResolver): HandlerMethodArgumentResolver {
        return object : HandlerMethodArgumentResolver {
            override fun supportsParameter(parameter: MethodParameter): Boolean {
                return argumentResolver.supportsParameter(parameter.parameter)
            }

            override fun resolveArgument(
                parameter: MethodParameter,
                bindingContext: BindingContext,
                exchange: ServerWebExchange
            ): Mono<in Any> {
                return argumentResolver.resolveArgument(parameter.parameter)
            }
        }
    }
}