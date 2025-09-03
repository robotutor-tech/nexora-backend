package com.robotutor.nexora.modules.iam.domain.repository

import com.robotutor.nexora.modules.iam.domain.entity.Role
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.RoleId
import com.robotutor.nexora.shared.domain.model.RoleType
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface RoleRepository {
    fun save(role: Role): Mono<Role>
    fun findAllByPremisesIdAndRoleIdIn(premisesId: PremisesId, roleIds: List<RoleId>): Flux<Role>
    fun findByRoleId(roleId: RoleId): Mono<Role>
    fun findAllByPremisesIdAndRoleTypeIn(premisesId: PremisesId, roleTypes: List<RoleType>): Flux<Role>
}