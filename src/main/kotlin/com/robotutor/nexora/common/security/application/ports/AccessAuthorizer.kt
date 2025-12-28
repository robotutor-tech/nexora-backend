package com.robotutor.nexora.common.security.application.ports

import com.robotutor.nexora.common.security.interfaces.annotation.HttpAuthorize
import com.robotutor.nexora.shared.domain.vo.ResourceId
import com.robotutor.nexora.common.security.interfaces.view.AuthorizedResources
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

interface AccessAuthorizer {
    fun authorize(httpAuthorize: HttpAuthorize, resourceId: ResourceId): Mono<Boolean>
    fun getAuthorizedScope(exchange: ServerWebExchange, httpAuthorize: HttpAuthorize): Mono<AuthorizedResources>
}