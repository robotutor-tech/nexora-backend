package com.robotutor.nexora.common.security.application

import com.robotutor.nexora.common.security.application.filters.PREMISES_ID
import com.robotutor.nexora.common.security.application.filters.START_TIME
import com.robotutor.nexora.common.security.application.filters.TRACE_ID
import com.robotutor.nexora.shared.logger.ReactiveContext.putPremisesId
import com.robotutor.nexora.shared.logger.ReactiveContext.putTraceId
import com.robotutor.nexora.shared.logger.models.ServerWebExchangeDTO
import org.springframework.web.server.ServerWebExchange
import reactor.util.context.Context
import java.time.Instant
import java.util.UUID.randomUUID

fun getTraceIdFromExchange(exchange: ServerWebExchange): String {
    return exchange.attributes[TRACE_ID] as? String
        ?: exchange.request.headers.getFirst(TRACE_ID)
        ?: randomUUID().toString()
}

fun getPremisesIdFromExchange(exchange: ServerWebExchange): String {
    return exchange.attributes[PREMISES_ID] as? String
        ?: exchange.request.headers.getFirst(PREMISES_ID)
        ?: "missing-premises-id"
}


fun writeContextOnChain(context: Context, exchange: ServerWebExchange): Context {
    val traceId = getTraceIdFromExchange(exchange)
    val premisesId = getPremisesIdFromExchange(exchange)
    val startTime = exchange.getAttribute(START_TIME) ?: Instant.now()
    val newContext = putTraceId(context, traceId)
    return putPremisesId(newContext, premisesId)
        .put(ServerWebExchangeDTO::class.java, ServerWebExchangeDTO.from(exchange))
        .put(START_TIME, startTime)
}
