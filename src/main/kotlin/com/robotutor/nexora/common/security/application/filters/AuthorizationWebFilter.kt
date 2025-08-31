package com.robotutor.nexora.common.security.application.filters

import com.robotutor.nexora.shared.application.annotation.RequireAccess
import com.robotutor.nexora.common.security.application.ports.EntitlementFacade
import com.robotutor.nexora.common.security.application.ports.OpaFacade
import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.common.security.domain.exceptions.NexoraError
import com.robotutor.nexora.common.security.domain.model.PolicyInput
import com.robotutor.nexora.shared.application.service.ContextDataResolver
import com.robotutor.nexora.shared.domain.exception.AccessDeniedException
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.ResourceContext
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
    private val opaFacade: OpaFacade,
    private val handlerMapping: RequestMappingHandlerMapping,
    private val entitlementFacade: EntitlementFacade
) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return handlerMapping.getHandler(exchange)
            .switchIfEmpty(chain.filter(exchange))
            .flatMap { handler ->
                if (handler is HandlerMethod) {
                    val requirePolicy = handler.getMethodAnnotation(RequireAccess::class.java)
                    if (requirePolicy == null) {
                        chain.filter(exchange)
                    } else {
                        validateAccess(exchange, requirePolicy)
                            .contextWrite { writeContextOnChain(it, exchange) }
                            .flatMap { allowed ->
                                if (allowed) chain.filter(exchange)
                                else createMonoError(AccessDeniedException(NexoraError.NEXORA0105))
                            }
                    }
                } else {
                    chain.filter(exchange)
                }
            }
    }


    private fun validateAccess(exchange: ServerWebExchange, requirePolicy: RequireAccess): Mono<Boolean> {
        return ContextDataResolver.getActorData()
            .flatMap { actorData ->
                entitlementFacade.getEntitlements(requirePolicy.action, requirePolicy.resource)
                    .collectList()
                    .flatMap { entitlements ->
                        val resourceId = resolveResourceId(exchange, requirePolicy.idParam) ?: "*"
                        val input = PolicyInput(
                            resource = ResourceContext(requirePolicy.resource, resourceId, requirePolicy.action),
                            premisesId = actorData.premisesId.value,
                            entitlements = entitlements
                        )
                        opaFacade.evaluate(input)
                    }
            }
    }

    private fun resolveResourceId(exchange: ServerWebExchange, param: String): String? {
        if (param.isBlank()) return null
        val pathVariables = exchange.getAttribute<Map<String, String>>(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)
        return pathVariables?.get(param) ?: exchange.request.queryParams[param]?.firstOrNull()
    }
}
