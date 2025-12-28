package com.robotutor.nexora.common.security.application.filters

import com.robotutor.nexora.common.security.application.ports.AccessAuthorizer
import com.robotutor.nexora.common.security.application.resolvers.SpringExpressionResourceIdResolver
import com.robotutor.nexora.common.security.application.writeContextOnChain
import com.robotutor.nexora.common.security.domain.exceptions.NexoraError
import com.robotutor.nexora.common.security.interfaces.annotation.HttpAuthorize
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
import com.robotutor.nexora.shared.utility.createMonoError
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
@Order(3)
class AuthorizationWebFilter(
    private val handlerMapping: RequestMappingHandlerMapping,
    private val accessAuthorizer: AccessAuthorizer,
    private val resourceIdResolver: SpringExpressionResourceIdResolver,
) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return handlerMapping.getHandler(exchange)
            .flatMap { handler ->
                (handler as? HandlerMethod)
                    ?.let { authorizeIfRequired(exchange, chain, it) }
                    ?: chain.filter(exchange)
            }
    }

    private fun authorizeIfRequired(
        exchange: ServerWebExchange,
        chain: WebFilterChain,
        handler: HandlerMethod,
    ): Mono<Void> {
        val httpAuthorize = handler.getMethodAnnotation(HttpAuthorize::class.java)
            ?: return chain.filter(exchange)

        val resourceId = resourceIdResolver.resolve(httpAuthorize, exchange, handler)
        return accessAuthorizer.authorize(httpAuthorize, resourceId)
            .contextWrite { context -> writeContextOnChain(context, exchange) }
            .flatMap { allowed ->
                if (allowed) chain.filter(exchange)
                else createMonoError(UnAuthorizedException(NexoraError.NEXORA0105))
            }
    }
}
