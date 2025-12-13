package com.robotutor.nexora.common.security.application.resolvers

import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.shared.application.service.ContextDataResolver
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Suppress("UNCHECKED_CAST")
@Component
class AccountDataResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == AccountData::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> {
        return ContextDataResolver.getAccountData() as Mono<Any>
    }
}
