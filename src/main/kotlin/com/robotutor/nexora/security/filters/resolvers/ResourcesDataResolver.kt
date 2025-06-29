package com.robotutor.nexora.security.filters.resolvers

import com.robotutor.nexora.security.createMono
import com.robotutor.nexora.security.filters.annotations.RequireAccess
import com.robotutor.nexora.security.gateway.IAMGateway
import com.robotutor.nexora.security.models.ResourcesData
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class ResourcesDataResolver(
    private val handlerMapping: RequestMappingHandlerMapping,
    private val iamGateway: IAMGateway
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == ResourcesData::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> {
        return handlerMapping.getHandler(exchange).flatMap { handler ->
            if (handler is HandlerMethod) {
                val requirePolicy = handler.getMethodAnnotation(RequireAccess::class.java)
                if (requirePolicy == null) {
                    createMono(ResourcesData(emptyList()))
                } else {
                    iamGateway.getEntitlements(requirePolicy.action, requirePolicy.resource)
                        .map { ResourcesData(it) }
                }
            } else {
                createMono(ResourcesData(emptyList()))
            }
        }
    }
}
