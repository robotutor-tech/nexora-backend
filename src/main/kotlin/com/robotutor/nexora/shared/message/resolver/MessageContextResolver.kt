package com.robotutor.nexora.shared.message.resolver

import com.robotutor.nexora.shared.context.ReactiveContext
import com.robotutor.nexora.shared.domain.exception.DataNotFoundException
import com.robotutor.nexora.shared.domain.exception.SharedNexoraError
import com.robotutor.nexora.shared.message.message.MessageContext
import com.robotutor.nexora.shared.resolver.ArgumentResolver
import com.robotutor.nexora.shared.utility.createMonoError
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.lang.reflect.Parameter

@Component
class MessageContextResolver : ArgumentResolver {
    override fun supportsParameter(parameter: Parameter): Boolean {
        return parameter.type == MessageContext::class.java
    }

    override fun resolveArgument(parameter: Parameter): Mono<Any> {
        @Suppress("UNCHECKED_CAST")
        return ReactiveContext.getMessageContext()
            .switchIfEmpty {
                createMonoError(DataNotFoundException(SharedNexoraError.NEXORA0104))
            } as Mono<Any>
    }
}
