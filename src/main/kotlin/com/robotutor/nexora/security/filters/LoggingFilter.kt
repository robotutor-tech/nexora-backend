package com.robotutor.nexora.security.filters

import com.robotutor.nexora.logger.LogDetails
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.models.RequestDetails
import com.robotutor.nexora.logger.models.ResponseDetails
import com.robotutor.nexora.logger.models.ServerWebExchangeDTO
import com.robotutor.nexora.security.getTraceId
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.ZoneOffset

@Component
@Order(1)
class LoggingFilter : WebFilter {
    val logger = Logger(this::class.java)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val startTime = LocalDateTime.now()
        val additionalDetails = mapOf("method" to exchange.request.method, "path" to exchange.request.uri.path)

        return chain.filter(exchange)
            .contextWrite { it.put(ServerWebExchangeDTO::class.java, ServerWebExchangeDTO.from(exchange)) }
            .doFinally {
                val logDetails = LogDetails.create(
                    message = "Successfully send api response",
                    traceId = getTraceId(exchange),
                    requestDetails = RequestDetails(
                        method = exchange.request.method,
                        headers = exchange.request.headers,
                        uriWithParams = exchange.request.uri.toString(),
                        body = exchange.request.body.toString()
                    ),
                    responseDetails = ResponseDetails(
                        headers = exchange.response.headers,
                        statusCode = exchange.response.statusCode.toString(),
                        time = (LocalDateTime.now()
                            .toEpochSecond(ZoneOffset.UTC) - startTime.toEpochSecond(ZoneOffset.UTC)) * 1000,
                        body = exchange.response.bufferFactory().toString()
                    ),
                    additionalDetails = additionalDetails
                )
                logger.info(logDetails)
            }
    }
}

