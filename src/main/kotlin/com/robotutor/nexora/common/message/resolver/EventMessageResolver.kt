package com.robotutor.nexora.common.message.resolver

import com.robotutor.nexora.common.message.annotation.Message
import com.robotutor.nexora.common.resolver.ArgumentResolver
import com.robotutor.nexora.shared.application.serialization.DefaultSerializer
import com.robotutor.nexora.shared.domain.exception.DataNotFoundException
import com.robotutor.nexora.shared.domain.exception.SharedNexoraError
import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.shared.utility.createMonoError
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.lang.reflect.Parameter


@Component
class EventMessageResolver : ArgumentResolver {
    override fun supportsParameter(parameter: Parameter): Boolean {
        return parameter.isAnnotationPresent(Message::class.java)
    }

    override fun resolveArgument(parameter: Parameter): Mono<Any> {
        return Mono.deferContextual { context ->
            val eventMessage = context.getOrDefault<String>("EventMessage", null)
            if (eventMessage != null) {
                createMono(DefaultSerializer.deserialize(eventMessage, parameter.type))
            } else {
                createMonoError(DataNotFoundException(SharedNexoraError.NEXORA0103))
            }
        }
    }
}
