package com.robotutor.nexora.modules.iam.domain.repository

import com.robotutor.nexora.modules.iam.domain.model.Role
import com.robotutor.nexora.shared.domain.model.RoleId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface RoleRepository {
    fun save(role: Role): Mono<Role>
    fun findAllByRoleIdIn(roles: List<RoleId>): Flux<Role>
    fun findByRoleId(roleId: RoleId): Mono<Role>
}