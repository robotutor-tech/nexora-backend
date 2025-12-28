package com.robotutor.nexora.common.security.application.filters

import com.robotutor.nexora.common.observability.models.RequestDetails
import com.robotutor.nexora.common.observability.models.ResponseDetails
import com.robotutor.nexora.common.security.application.getCorrelationIdFromExchange
import com.robotutor.nexora.common.security.application.getPremisesIdFromExchange
import com.robotutor.nexora.common.security.application.writeContextOnChain
import com.robotutor.nexora.shared.application.logger.LogDetails
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.ReactiveContext.CORRELATION_ID
import com.robotutor.nexora.shared.application.logger.ReactiveContext.START_TIME
import com.robotutor.nexora.shared.application.logger.ReactiveContext.X_PREMISES_ID
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.time.Instant

@Component
@Order(1)
class LoggingFilter : WebFilter {
    val logger = Logger(this::class.java)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val startTime = Instant.now()

        val premisesId = getPremisesIdFromExchange(exchange)
        val correlationId = getCorrelationIdFromExchange(exchange)

        exchange.attributes[START_TIME] = startTime
        exchange.attributes[X_PREMISES_ID] = premisesId
        exchange.attributes[CORRELATION_ID] = correlationId

        exchange.response.headers.add(CORRELATION_ID, correlationId)
        return chain.filter(exchange)
            .contextWrite { writeContextOnChain(it, exchange) }
            .doFinally {
                val requestDetails = RequestDetails(
                    method = exchange.request.method,
                    uri = exchange.request.uri.toString()
                )
                val responseDetails = ResponseDetails(
                    statusCode = exchange.response.statusCode.toString(),
                    time = (Instant.now().epochSecond - startTime.epochSecond) * 1000,
                )
                val additionalDetails = mapOf("request" to requestDetails, "response" to responseDetails)
                val logDetails = LogDetails(
                    message = "Successfully responded to http request",
                    premisesId = premisesId,
                    correlationId = correlationId,
                    additionalDetails = additionalDetails
                )
                logger.info(logDetails)
            }
    }
}

