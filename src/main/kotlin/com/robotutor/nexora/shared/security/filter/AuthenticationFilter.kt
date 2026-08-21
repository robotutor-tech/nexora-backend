package com.robotutor.nexora.shared.security.filter

import com.robotutor.nexora.shared.security.service.ContextService
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

const val CONTEXT_HEADER = "x-context-data"

@Component
@Order(2)
class AuthenticationFilter(private val contextService: ContextService, private val routeValidator: RouteValidator) :
    WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        if (exchange.request.uri.path.startsWith("/api")) {
            return chain.filter(exchange)
        }
        if (routeValidator.isUnsecured(exchange.request)) {
            return chain.filter(exchange)
        }
        val token = exchange.request.headers.getFirst(CONTEXT_HEADER)
        val principalData = contextService.getPrincipalData(token)
        val authentication = UsernamePasswordAuthenticationToken(principalData, null, emptyList())
        val securityContext = createMono(SecurityContextImpl(authentication))
        return chain.filter(exchange)
            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(securityContext))
    }
}
