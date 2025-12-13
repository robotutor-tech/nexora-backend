package com.robotutor.nexora.shared.infrastructure.messaging.resolver

import com.robotutor.nexora.shared.application.service.ContextDataResolver
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.infrastructure.messaging.services.impl.KafkaArgumentResolver
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.lang.reflect.Parameter

@Component
class ActorDataKafkaArgumentResolver : KafkaArgumentResolver {
    override fun supportsParameter(parameter: Parameter): Boolean {
        return parameter.type == ActorData::class.java
    }

    override fun resolveArgument(parameter: Parameter, event: Any): Mono<Any> {
        @Suppress("UNCHECKED_CAST")
        return ContextDataResolver.getActorData() as Mono<Any>
    }
}
