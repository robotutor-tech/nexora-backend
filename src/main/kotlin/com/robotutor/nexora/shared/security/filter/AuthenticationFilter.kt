package com.robotutor.nexora.shared.security.filter

import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.serialization.DefaultSerializer.serialize
import com.robotutor.nexora.shared.domain.exception.BaseException
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
class AuthenticationFilter(
    private val routeValidator: RouteValidator,
    private val exceptionHandlerRegistry: ExceptionHandlerRegistry,
    private val jwtValidationService: JwtValidationService,
) : WebFilter {
    val logger = Logger(this::class.java)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val token = routeValidator.getToken(exchange.request)
        return jwtValidationService.validate(token ?: "")
            .map { principalData ->
                val authentication = UsernamePasswordAuthenticationToken(principalData, null, emptyList())
                SecurityContextImpl(authentication)
            }
            .flatMap { securityContext ->
                chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(createMono(securityContext)))
            }
            .onErrorResume(BaseException::class.java) { ex ->
                if (routeValidator.isUnsecured(exchange.request)) {
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
