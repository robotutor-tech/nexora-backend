package com.robotutor.nexora.common.security.resolver

import com.robotutor.nexora.common.resolver.AccountDataResolver
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class HandlerAccountDataResolver(private val accountDataResolver: AccountDataResolver) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return accountDataResolver.supportsParameter(parameter.parameter)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<in Any> {
        return accountDataResolver.resolveArgument()
    }
}