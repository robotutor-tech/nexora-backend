package com.robotutor.nexora.common.security.application

import com.robotutor.nexora.shared.application.logger.ReactiveContext.CORRELATION_ID
import com.robotutor.nexora.shared.application.logger.ReactiveContext.START_TIME
import com.robotutor.nexora.shared.application.logger.ReactiveContext.X_PREMISES_ID
import com.robotutor.nexora.shared.application.logger.ReactiveContext.putCorrelationId
import com.robotutor.nexora.shared.application.logger.ReactiveContext.putPremisesId
import com.robotutor.nexora.shared.application.logger.ReactiveContext.putStartTime
import com.robotutor.nexora.shared.domain.vo.PremisesId
import org.springframework.http.HttpHeaders
import org.springframework.web.server.ServerWebExchange
import reactor.util.context.Context
import java.time.Instant
import java.util.UUID.randomUUID


fun getCorrelationIdFromExchange(exchange: ServerWebExchange): String {
    return exchange.attributes[CORRELATION_ID] as? String
        ?: exchange.request.headers.getFirst(CORRELATION_ID)
        ?: randomUUID().toString()
}

fun getPremisesIdFromExchange(exchange: ServerWebExchange): String {
    return exchange.attributes[X_PREMISES_ID] as? String
        ?: exchange.request.headers.getFirst(X_PREMISES_ID)
        ?: "missing-premises-id"
}

fun getStartTimeFromExchange(exchange: ServerWebExchange): Instant {
    return exchange.attributes[START_TIME] as? Instant
        ?: Instant.now()
}


fun writeContextOnChain(context: Context, exchange: ServerWebExchange): Context {
    val premisesId = getPremisesIdFromExchange(exchange)
    val correlationId = getCorrelationIdFromExchange(exchange)
    val startTime = getStartTimeFromExchange(exchange)
    var newContext = putPremisesId(context, PremisesId(premisesId))
    newContext = putCorrelationId(newContext, correlationId)
    newContext = putStartTime(newContext, startTime)
    return newContext.put(HttpHeaders::class.java, exchange.request.headers)
}
