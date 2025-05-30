package com.robotutor.nexora.redis.services

import com.robotutor.nexora.security.getTraceId
import org.springframework.web.server.ServerWebExchange

fun getRedisKey(exchange: ServerWebExchange? = null): String {
    val suffix = exchange?.let { ":${getTraceId(it)}" } ?: ""
    val stackTrace = Throwable().stackTrace
    val caller = stackTrace[1]
    return "${caller.className}.${caller.methodName}${suffix}"
}