package com.robotutor.nexora.shared.infrastructure.messaging.services.impl

import reactor.core.publisher.Mono
import java.lang.reflect.Parameter

interface KafkaArgumentResolver {
    fun supportsParameter(parameter: Parameter): Boolean
    fun resolveArgument(parameter: Parameter, event: Any): Mono<Any>
}