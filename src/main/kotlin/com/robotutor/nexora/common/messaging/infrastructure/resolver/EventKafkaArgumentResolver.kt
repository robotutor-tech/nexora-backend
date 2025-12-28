package com.robotutor.nexora.common.messaging.infrastructure.resolver

import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.common.messaging.infrastructure.annotation.KafkaEvent
import com.robotutor.nexora.common.messaging.infrastructure.services.impl.KafkaArgumentResolver
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.lang.reflect.Parameter


@Component
class EventKafkaArgumentResolver : KafkaArgumentResolver {
    override fun supportsParameter(parameter: Parameter): Boolean {
        return parameter.isAnnotationPresent(KafkaEvent::class.java)
    }

    override fun resolveArgument(parameter: Parameter, event: Any): Mono<Any> {
        return createMono(event)
    }
}
