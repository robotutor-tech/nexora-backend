package com.robotutor.nexora.common.security.application.ports

import com.robotutor.nexora.shared.application.annotation.RequireAccess
import com.robotutor.nexora.shared.domain.vo.ResourceId
import reactor.core.publisher.Mono

interface AccessAuthorizer {
    fun authorize(requireAccess: RequireAccess, resourceId: ResourceId): Mono<Boolean>
}