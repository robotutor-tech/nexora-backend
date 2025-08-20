package com.robotutor.nexora.common.security.application.filters

import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.common.security.application.annotations.ActionType
import com.robotutor.nexora.common.security.application.annotations.ResourceType

//@Component
//@Order(3)
//class AuthorizationWebFilter(
//    private val opaClient: OpaClient,
//    private val handlerMapping: RequestMappingHandlerMapping,
//    private val iamGateway: IAMGateway
//) : WebFilter {
//    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
//        return handlerMapping.getHandler(exchange)
//            .flatMap { handler ->
//                if (handler is HandlerMethod) {
//                    val requirePolicy = handler.getMethodAnnotation(RequireAccess::class.java)
//                    if (requirePolicy == null) {
//                        chain.filter(exchange)
//                    } else {
//                        validateAccess(exchange, requirePolicy)
//                            .contextWrite { writeContextOnChain(it, exchange) }
//                            .flatMap { allowed ->
//                                if (allowed) chain.filter(exchange)
//                                else createMonoError(AccessDeniedException(NexoraError.NEXORA0105))
//                            }
//                    }
//                } else {
//                    chain.filter(exchange)
//                }
//            }
//    }
//
//
//    private fun validateAccess(exchange: ServerWebExchange, requirePolicy: RequireAccess): Mono<Boolean> {
//        return Mono.deferContextual { ctx ->
//            val premisesActorDataOptional = ctx.getOrEmpty<PremisesActorData>(PremisesActorData::class.java)
//            if (premisesActorDataOptional.isEmpty) {
//                createMonoError(UnAuthorizedException(NexoraError.NEXORA0101))
//            } else {
//                createMono(premisesActorDataOptional.get())
//            }
//        }
//            .flatMap { premisesActorData ->
//                iamGateway.getEntitlements(requirePolicy.action, requirePolicy.resource)
//                    .flatMap { entitlements ->
//                        val resourceId = resolveResourceId(exchange, requirePolicy.idParam) ?: "*"
//                        val input = PolicyInput(
//                            resource = ResourceContext(requirePolicy.resource, resourceId, requirePolicy.action),
//                            premisesId = premisesActorData.premisesId,
//                            entitlements = entitlements
//                        )
//                        opaClient.evaluate(input)
//                    }
//            }
//    }
//
//    private fun resolveResourceId(exchange: ServerWebExchange, param: String): String? {
//        if (param.isBlank()) return null
//        val pathVariables = exchange.getAttribute<Map<String, String>>(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)
//        return pathVariables?.get(param) ?: exchange.request.queryParams[param]?.firstOrNull()
//    }
//}

data class PolicyInput(
    val resource: ResourceContext,
    val premisesId: PremisesId,
    val entitlements: List<ResourceEntitlement>
)

data class ResourceContext(val type: ResourceType, val id: String, val action: ActionType)

data class ResourceEntitlement(
    val resource: ResourceContext, val premisesId: String
)