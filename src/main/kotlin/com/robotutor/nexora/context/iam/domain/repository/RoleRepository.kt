package com.robotutor.nexora.context.iam.domain.repository

import com.robotutor.nexora.context.iam.domain.aggregate.RoleAggregate
import com.robotutor.nexora.context.iam.domain.vo.RoleId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface RoleRepository {
    fun save(roleAggregate: RoleAggregate): Mono<RoleAggregate>
    fun findAllByRoleIds(roleIds: Set<RoleId>): Flux<RoleAggregate>
}