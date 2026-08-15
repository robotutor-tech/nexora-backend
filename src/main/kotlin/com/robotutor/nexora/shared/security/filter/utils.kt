package com.robotutor.nexora.shared.security.filter

import com.robotutor.nexora.shared.context.ReactiveContext.CORRELATION_ID
import org.springframework.web.server.ServerWebExchange
import java.util.UUID.randomUUID

fun getCorrelationIdFromExchange(exchange: ServerWebExchange): String {
    return exchange.attributes[CORRELATION_ID] as? String
        ?: exchange.request.headers.getFirst(CORRELATION_ID)
        ?: randomUUID().toString()
}

