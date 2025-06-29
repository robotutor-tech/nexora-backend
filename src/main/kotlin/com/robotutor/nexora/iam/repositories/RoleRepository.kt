package com.robotutor.nexora.iam.repositories

import com.robotutor.nexora.iam.models.Role
import com.robotutor.nexora.iam.models.RoleId
import com.robotutor.nexora.iam.models.RoleType
import com.robotutor.nexora.premises.models.PremisesId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface RoleRepository : ReactiveCrudRepository<Role, RoleId> {
    fun findByRoleId(roleId: RoleId): Mono<Role>
    fun findAllByRoleIdIn(roleIds: List<RoleId>): Flux<Role>
}
