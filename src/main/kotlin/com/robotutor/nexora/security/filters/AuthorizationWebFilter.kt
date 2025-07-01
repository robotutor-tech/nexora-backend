package com.robotutor.nexora.security.filters

import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.createMono
import com.robotutor.nexora.security.createMonoError
import com.robotutor.nexora.security.exceptions.NexoraError
import com.robotutor.nexora.security.filters.annotations.ActionType
import com.robotutor.nexora.security.filters.annotations.RequireAccess
import com.robotutor.nexora.security.filters.annotations.ResourceType
import com.robotutor.nexora.security.gateway.IAMGateway
import com.robotutor.nexora.security.gateway.OpaClient
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.webClient.exceptions.AccessDeniedException
import com.robotutor.nexora.webClient.exceptions.UnAuthorizedException
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
    private val opaClient: OpaClient,
    private val handlerMapping: RequestMappingHandlerMapping,
    private val iamGateway: IAMGateway
) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return handlerMapping.getHandler(exchange)
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
        return Mono.deferContextual { ctx ->
            val premisesActorDataOptional = ctx.getOrEmpty<PremisesActorData>(PremisesActorData::class.java)
            if (premisesActorDataOptional.isEmpty) {
                createMonoError(UnAuthorizedException(NexoraError.NEXORA0101))
            } else {
                createMono(premisesActorDataOptional.get())
            }
        }
            .flatMap { premisesActorData ->
                iamGateway.getEntitlements(requirePolicy.action, requirePolicy.resource)
                    .flatMap { entitlements ->
                        val resourceId = resolveResourceId(exchange, requirePolicy.idParam) ?: "*"
                        val input = PolicyInput(
                            resource = ResourceContext(requirePolicy.resource, resourceId, requirePolicy.action),
                            premisesId = premisesActorData.premisesId,
                            entitlements = entitlements
                        )
                        opaClient.evaluate(input)
                    }
            }
    }

    private fun resolveResourceId(exchange: ServerWebExchange, param: String): String? {
        if (param.isBlank()) return null
        val pathVariables = exchange.getAttribute<Map<String, String>>(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)
        return pathVariables?.get(param) ?: exchange.request.queryParams[param]?.firstOrNull()
    }
}

data class PolicyInput(
    val resource: ResourceContext,
    val premisesId: PremisesId,
    val entitlements: List<ResourceEntitlement>
)

data class ResourceContext(val type: ResourceType, val id: String, val action: ActionType)

data class ResourceEntitlement(
    val resource: ResourceContext, val premisesId: String
)