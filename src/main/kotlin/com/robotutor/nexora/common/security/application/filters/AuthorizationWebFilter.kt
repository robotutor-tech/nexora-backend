package com.robotutor.nexora.common.security.application.filters

import com.robotutor.nexora.common.security.application.ports.AccessAuthorizer
import com.robotutor.nexora.common.security.domain.exceptions.NexoraError
import com.robotutor.nexora.shared.application.annotation.Authorize
import com.robotutor.nexora.shared.application.annotation.ResourceId
import com.robotutor.nexora.shared.application.annotation.ResourceSelector
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
import com.robotutor.nexora.shared.utility.createMonoError
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.reactive.HandlerMapping
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
) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return handlerMapping.getHandler(exchange)
            .switchIfEmpty(chain.filter(exchange))
            .flatMap { handler ->
                if (handler is HandlerMethod) {
                    val authorize = handler.getMethodAnnotation(Authorize::class.java)
                    if (authorize == null) {
                        chain.filter(exchange)
                    } else {
                        val resourceId =
                            if (authorize.resourceSelector == ResourceSelector.ALL)
                                com.robotutor.nexora.shared.domain.vo.ResourceId.ALL
                            else resolveResourceId(exchange, handler)
                        accessAuthorizer.authorize(authorize, resourceId)
                            .contextWrite { context -> writeContextOnChain(context, exchange) }
                            .flatMap { allowed ->
                                if (allowed) chain.filter(exchange)
                                else
                                    createMonoError(UnAuthorizedException(NexoraError.NEXORA0105))
                            }
                    }
                } else {
                    chain.filter(exchange)
                }
            }
    }

    private fun resolveResourceId(
        exchange: ServerWebExchange,
        handler: HandlerMethod
    ): com.robotutor.nexora.shared.domain.vo.ResourceId {
        val resourceParamName = handler.methodParameters
            .firstOrNull { it.hasParameterAnnotation(ResourceId::class.java) }
            ?.parameter?.name

        val uriVariables = exchange.getAttribute<Map<String, String>>(
            HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE
        ) ?: emptyMap()

        val resourceId = (uriVariables[resourceParamName])
            ?: throw IllegalStateException("Path variable '$resourceParamName' not found in request")
        return com.robotutor.nexora.shared.domain.vo.ResourceId(resourceId)
    }
}
