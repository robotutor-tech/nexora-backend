package com.robotutor.nexora.context.iam.domain.repository

import com.robotutor.nexora.context.iam.domain.aggregate.RoleAggregate
import reactor.core.publisher.Mono

interface RoleRepository {
    fun save(roleAggregate: RoleAggregate): Mono<RoleAggregate>
}