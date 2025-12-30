package com.robotutor.nexora.common.messaging.resolver

import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.common.messaging.annotation.KafkaEvent
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.lang.reflect.Parameter


@Component
class EventArgumentResolver {
    fun supportsParameter(parameter: Parameter): Boolean {
        return parameter.isAnnotationPresent(KafkaEvent::class.java)
    }

    fun resolveArgument(event: Any): Mono<Any> {
        return createMono(event)
    }
}
