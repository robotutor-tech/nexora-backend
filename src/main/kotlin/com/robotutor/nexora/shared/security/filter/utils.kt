package com.robotutor.nexora.shared.security.filter

import com.robotutor.nexora.shared.application.logger.ReactiveContext.CORRELATION_ID
import com.robotutor.nexora.shared.application.logger.ReactiveContext.putCorrelationId
import org.springframework.http.HttpHeaders
import org.springframework.web.server.ServerWebExchange
import reactor.util.context.Context
import java.util.UUID.randomUUID

fun getCorrelationIdFromExchange(exchange: ServerWebExchange): String {
    return exchange.attributes[CORRELATION_ID] as? String
        ?: exchange.request.headers.getFirst(CORRELATION_ID)
        ?: randomUUID().toString()
}

fun writeContextOnChain(context: Context, exchange: ServerWebExchange): Context {
    val correlationId = getCorrelationIdFromExchange(exchange)
    val newContext = putCorrelationId(context, correlationId)
    return newContext.put(HttpHeaders::class.java, exchange.request.headers)
}
