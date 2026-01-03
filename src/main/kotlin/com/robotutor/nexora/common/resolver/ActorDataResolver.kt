package com.robotutor.nexora.common.resolver

import com.robotutor.nexora.shared.domain.exception.DataNotFoundException
import com.robotutor.nexora.shared.domain.exception.SharedNexoraError
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.shared.utility.createMonoError
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.lang.reflect.Parameter

@Component
class ActorDataResolver : ArgumentResolver {
    override fun supportsParameter(parameter: Parameter): Boolean {
        return parameter.type == ActorData::class.java
    }

    override fun resolveArgument(parameter: Parameter): Mono<Any> {
        return Mono.deferContextual { context ->
            val optional = context.getOrEmpty<ActorData>(ActorData::class.java)
            if (optional.isPresent) {
                createMono(optional.get())
            } else {
                createMonoError(DataNotFoundException(SharedNexoraError.NEXORA0101))
            }
        }
    }
}
