package com.robotutor.nexora.iam.repositories

import com.robotutor.nexora.iam.models.Role
import com.robotutor.nexora.iam.models.RoleId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface RoleRepository : ReactiveCrudRepository<Role, RoleId> {
    fun findByRoleId(roleId: RoleId): Mono<Role>
}
