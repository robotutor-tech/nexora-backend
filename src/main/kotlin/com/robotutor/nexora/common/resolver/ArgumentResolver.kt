package com.robotutor.nexora.common.resolver

import reactor.core.publisher.Mono
import java.lang.reflect.Parameter

interface ArgumentResolver {
    fun supportsParameter(parameter: Parameter): Boolean
    fun resolveArgument(): Mono<Any>
}