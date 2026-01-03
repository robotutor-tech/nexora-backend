package com.robotutor.nexora.common.resolver

import com.robotutor.nexora.shared.domain.exception.DataNotFoundException
import com.robotutor.nexora.shared.domain.exception.SharedNexoraError
import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.shared.utility.createMonoError
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.lang.reflect.Parameter

@Component
class AccountDataResolver : ArgumentResolver {
    override fun supportsParameter(parameter: Parameter): Boolean {
        return parameter.type == AccountData::class.java
    }

    override fun resolveArgument(parameter: Parameter): Mono<Any> {
        return Mono.deferContextual { context ->
            val accountDataDataOptional = context.getOrEmpty<AccountData>(AccountData::class.java)
            if (accountDataDataOptional.isPresent) {
                createMono(accountDataDataOptional.get())
            } else {
                createMonoError(DataNotFoundException(SharedNexoraError.NEXORA0102))
            }
        }
    }
}
