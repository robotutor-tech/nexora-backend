package com.robotutor.nexora.shared.security.filter

import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.serialization.DefaultSerializer.serialize
import com.robotutor.nexora.shared.security.controllers.ExceptionHandlerRegistry
import com.robotutor.nexora.shared.security.service.JwtValidationService
import com.robotutor.nexora.shared.utility.createMono
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
@Order(1)
class GatewayAuthenticationFilter(
    private val routeValidator: RouteValidator,
    private val exceptionHandlerRegistry: ExceptionHandlerRegistry,
    private val jwtValidationService: JwtValidationService,
) : WebFilter {
    val logger = Logger(this::class.java)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        if (!exchange.request.uri.path.startsWith("/api")) {
            return chain.filter(exchange)
        }
        try {
            val token = routeValidator.getToken(exchange.request)
            val principalData = jwtValidationService.validate(token ?: "")
            val authentication = UsernamePasswordAuthenticationToken(principalData, null, emptyList())
            val securityContext = createMono(SecurityContextImpl(authentication))
            return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(securityContext))
        } catch (ex: Throwable) {
            return if (routeValidator.isUnsecured(exchange.request)) {
                chain.filter(exchange)
            } else {
                val responseEntity = exceptionHandlerRegistry.handle(ex)
                exchange.response.statusCode = responseEntity.statusCode
                val buffer = exchange.response.bufferFactory().wrap(
                    serialize(responseEntity.body).toByteArray(),
                )
                exchange.response.writeWith(Mono.just(buffer))
            }
        }
    }
}
