package com.robotutor.nexora.shared.resolver

import com.robotutor.nexora.shared.domain.exception.DataNotFoundException
import com.robotutor.nexora.shared.domain.exception.SharedNexoraError
import com.robotutor.nexora.shared.domain.vo.PrincipalData
import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.shared.utility.createMonoError
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.lang.reflect.Parameter
import kotlin.jvm.java

@Component
class AccountDataResolver : ArgumentResolver {
    override fun supportsParameter(parameter: Parameter): Boolean {
        return parameter.type == PrincipalData::class.java
    }

    override fun resolveArgument(parameter: Parameter): Mono<Any> {
        return Mono.deferContextual { context ->
            val principalDataDataOptional = context.getOrEmpty<PrincipalData>(PrincipalData::class.java)
            if (principalDataDataOptional.isPresent) {
                createMono(principalDataDataOptional.get())
            } else {
                createMonoError(DataNotFoundException(SharedNexoraError.NEXORA0102))
            }
        }
    }
}
