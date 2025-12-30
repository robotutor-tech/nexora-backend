package com.robotutor.nexora.common.resolver

import com.robotutor.nexora.shared.application.ReactiveContext
import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.lang.reflect.Parameter

@Component
class AccountDataResolver : ArgumentResolver {
    override fun supportsParameter(parameter: Parameter): Boolean {
        return parameter.type == AccountData::class.java
    }

    override fun resolveArgument(): Mono<Any> {
        @Suppress("UNCHECKED_CAST")
        return ReactiveContext.getAccountData() as Mono<Any>
    }
}
