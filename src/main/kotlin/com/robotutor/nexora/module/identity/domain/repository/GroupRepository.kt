package com.robotutor.nexora.module.identity.domain.repository

import com.robotutor.nexora.module.identity.domain.aggregate.GroupAggregate
import com.robotutor.nexora.module.identity.domain.vo.GroupId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface GroupRepository {
    fun save(groupAggregate: GroupAggregate): Mono<GroupAggregate>
    fun saveAll(groupAggregates: List<GroupAggregate>): Flux<GroupAggregate>
    fun findAllByGroupIds(groupIds: Set<GroupId>): Flux<GroupAggregate>
}
