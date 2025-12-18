package com.robotutor.nexora.common.security.application.ports

import com.robotutor.nexora.shared.application.annotation.Authorize
import com.robotutor.nexora.shared.domain.vo.ResourceId
import com.robotutor.nexora.shared.interfaces.view.AuthorizedResources
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

interface AccessAuthorizer {
    fun authorize(authorize: Authorize, resourceId: ResourceId): Mono<Boolean>
    fun getAuthorizedScope(exchange: ServerWebExchange, authorize: Authorize): Mono<AuthorizedResources>
}