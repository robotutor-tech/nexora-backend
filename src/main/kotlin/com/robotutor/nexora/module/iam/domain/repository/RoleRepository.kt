package com.robotutor.nexora.module.iam.domain.repository

import com.robotutor.nexora.module.iam.domain.aggregate.RoleAggregate
import com.robotutor.nexora.module.iam.domain.vo.RoleId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface RoleRepository {
    fun save(roleAggregate: RoleAggregate): Mono<RoleAggregate>
    fun saveAll(roleAggregates: List<RoleAggregate>): Flux<RoleAggregate>
    fun findAllByRoleIds(roleIds: Set<RoleId>): Flux<RoleAggregate>
}