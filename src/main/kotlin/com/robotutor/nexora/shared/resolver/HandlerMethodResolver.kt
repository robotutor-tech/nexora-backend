package com.robotutor.nexora.shared.resolver

import org.springframework.core.MethodParameter
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

object HandlerMethodResolver {
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
