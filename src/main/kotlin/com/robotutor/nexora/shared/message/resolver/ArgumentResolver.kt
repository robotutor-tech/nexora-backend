package com.robotutor.nexora.shared.message.resolver

import reactor.core.publisher.Mono
import java.lang.reflect.Parameter

interface ArgumentResolver {
    fun supportsParameter(parameter: Parameter): Boolean

    fun resolveArgument(parameter: Parameter): Mono<Any>
}
