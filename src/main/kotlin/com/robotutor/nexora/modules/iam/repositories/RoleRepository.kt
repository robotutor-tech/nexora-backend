package com.robotutor.nexora.modules.iam.repositories

import com.robotutor.nexora.modules.iam.models.Role
import com.robotutor.nexora.modules.iam.models.RoleId
import com.robotutor.nexora.modules.iam.models.RoleType
import com.robotutor.nexora.modules.premises.models.PremisesId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface RoleRepository : ReactiveCrudRepository<Role, RoleId> {
    fun findByRoleId(roleId: RoleId): Mono<Role>
    fun findAllByRoleIdIn(roleIds: List<RoleId>): Flux<Role>
    fun findAllByPremisesIdAndRoleIn(premisesId: PremisesId, roles: List<RoleType>): Flux<Role>
}
