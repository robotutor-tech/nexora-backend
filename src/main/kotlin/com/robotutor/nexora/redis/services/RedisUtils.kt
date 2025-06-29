package com.robotutor.nexora.redis.services

import com.robotutor.nexora.logger.ReactiveContext.getTraceId
import com.robotutor.nexora.security.createMono
import reactor.core.publisher.Mono

fun getRedisKey(key: String): Mono<String> {
    val stack = Throwable().stackTrace[3]
    return Mono.deferContextual { ctx ->
        val traceId = getTraceId(ctx)
        createMono("${stack.className}.${stack.methodName}:${key}::$traceId")
    }
}