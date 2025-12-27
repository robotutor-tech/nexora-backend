package com.robotutor.nexora.common.security.application.resolvers

import com.robotutor.nexora.common.security.application.ports.AccessAuthorizer
import com.robotutor.nexora.shared.interfaces.annotation.HttpAuthorize
import com.robotutor.nexora.shared.interfaces.view.AuthorizedResources
import com.robotutor.nexora.shared.utility.createMonoError
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class AuthorizedResourcesResolver(
    private val handlerMapping: RequestMappingHandlerMapping,
    private val accessAuthorizer: AccessAuthorizer
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == AuthorizedResources::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> {
        return handlerMapping.getHandler(exchange).flatMap { handler ->
            if (handler is HandlerMethod) {
                val httpAuthorize = handler.getMethodAnnotation(HttpAuthorize::class.java)
                if (httpAuthorize == null) {
                    createMonoError(IllegalStateException("Authorize annotation is missing"))
                } else {
                    accessAuthorizer.getAuthorizedScope(exchange, httpAuthorize)
                }
            } else {
                createMonoError(IllegalStateException("not an handler Authorize annotation is missing"))
            }
        }
    }
}
