package com.robotutor.nexora.common.messaging.services.impl

import reactor.core.publisher.Mono
import java.lang.reflect.Parameter

interface KafkaArgumentResolver {
    fun supportsParameter(parameter: Parameter): Boolean
    fun resolveArgument(parameter: Parameter, event: Any): Mono<Any>
}