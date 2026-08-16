package com.robotutor.nexora.module.identity.domain.repository

import com.robotutor.nexora.module.identity.domain.aggregate.Role
import com.robotutor.nexora.module.identity.domain.vo.RoleId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface RoleRepository {
    fun save(role: Role): Mono<Role>
    fun saveAll(roles: List<Role>): Flux<Role>
    fun findAllByRoleIds(roleIds: Set<RoleId>): Flux<Role>
}
