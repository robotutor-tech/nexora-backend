package com.robotutor.nexora.security.filters

import com.robotutor.nexora.logger.LogDetails
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.ReactiveContext.getTraceId
import com.robotutor.nexora.logger.ReactiveContext.putTraceId
import com.robotutor.nexora.logger.models.RequestDetails
import com.robotutor.nexora.logger.models.ResponseDetails
import com.robotutor.nexora.logger.models.ServerWebExchangeDTO
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.ZoneOffset

@Component
@Order(1)
class LoggingFilter : WebFilter {
    val logger = Logger(this::class.java)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val startTime = Instant.now()
        val additionalDetails = mapOf("method" to exchange.request.method, "path" to exchange.request.uri.path)

        return Mono.deferContextual { ctx ->
            val traceId = getTraceId(ctx)
            exchange.response.headers.add("x-trace-id", traceId)
            chain.filter(exchange)
                .contextWrite { it.put(ServerWebExchangeDTO::class.java, ServerWebExchangeDTO.from(exchange)) }
                .contextWrite { putTraceId(it, traceId) }
                .doFinally {
                    val logDetails = LogDetails.create(
                        message = "Successfully send api response",
                        traceId = traceId,
                        requestDetails = RequestDetails(
                            method = exchange.request.method,
                            headers = exchange.request.headers,
                            uriWithParams = exchange.request.uri.toString(),
                            body = exchange.request.body.toString()
                        ),
                        responseDetails = ResponseDetails(
                            headers = exchange.response.headers,
                            statusCode = exchange.response.statusCode.toString(),
                            time = (Instant.now().epochSecond - startTime.epochSecond) * 1000,
                            body = exchange.response.bufferFactory().toString()
                        ),
                        additionalDetails = additionalDetails
                    )
                    logger.info(logDetails)
                }
        }
    }
}

