package com.robotutor.nexora.shared.security.filter

import com.robotutor.nexora.shared.application.logger.LogDetails
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.context.ReactiveContext
import com.robotutor.nexora.shared.context.ReactiveContext.CORRELATION_ID
import com.robotutor.nexora.shared.domain.vo.PrincipalData
import com.robotutor.nexora.shared.security.vo.RequestDetails
import com.robotutor.nexora.shared.security.vo.ResponseDetails
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.Instant

@Component
@Order(2)
class LoggingFilter : WebFilter {
    val logger = Logger(this::class.java)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val startTime = Instant.now()

        return ReactiveContext.getPrincipalData()
            .flatMap { accountData -> logRequestAndResponse(exchange, accountData, chain, startTime) }
            .switchIfEmpty { logRequestAndResponse(exchange, null, chain, startTime) }
    }

    private fun logRequestAndResponse(
        exchange: ServerWebExchange,
        principalData: PrincipalData?,
        chain: WebFilterChain,
        startTime: Instant
    ): Mono<Void> {
        val requestDetails = RequestDetails(exchange.request.method.toString(), exchange.request.uri.toString())
        val correlationId = getCorrelationIdFromExchange(exchange)

        exchange.attributes[CORRELATION_ID] = correlationId
        exchange.response.headers.add(CORRELATION_ID, correlationId)

        val logDetails = LogDetails(
            message = "Received request for http request",
            additionalDetails = mapOf("request" to requestDetails),
            correlationId = correlationId,
            principalData = principalData
        )

        logger.info(logDetails)

        return chain.filter(exchange)
            .contextWrite {
                val newContext = it.put(CORRELATION_ID, correlationId)
                newContext.put(HttpHeaders::class.java, exchange.request.headers)
            }
            .doFinally {
                val responseDetails = ResponseDetails(
                    statusCode = exchange.response.statusCode.toString(),
                    time = (Instant.now().epochSecond - startTime.epochSecond) * 1000,
                )
                val additionalDetails = mapOf("request" to requestDetails, "response" to responseDetails)
                val responseLogDetails = LogDetails(
                    message = "Successfully responded to http request",
                    additionalDetails = additionalDetails,
                    correlationId = correlationId,
                    principalData = principalData
                )
                logger.info(responseLogDetails)
            }
    }
}

