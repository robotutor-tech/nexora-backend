package com.robotutor.nexora.shared.resolver

import com.robotutor.nexora.shared.context.ReactiveContext
import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.shared.domain.vo.ActorData
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.lang.reflect.Parameter

@Component
class AccountDataResolver : ArgumentResolver {
    override fun supportsParameter(parameter: Parameter): Boolean {
        return parameter.type == AccountData::class.java
    }

    override fun resolveArgument(parameter: Parameter): Mono<Any> {
        return ReactiveContext.getPrincipalData()
            .map {
                when (it) {
                    is AccountData -> it
                    is ActorData -> it.accountData
                }
            }
    }
}
