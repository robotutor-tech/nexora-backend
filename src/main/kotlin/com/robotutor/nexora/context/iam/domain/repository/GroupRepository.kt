package com.robotutor.nexora.context.iam.domain.repository

import com.robotutor.nexora.context.iam.domain.aggregate.GroupAggregate
import com.robotutor.nexora.context.iam.domain.vo.GroupId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface GroupRepository {
    fun save(groupAggregate: GroupAggregate): Mono<GroupAggregate>
    fun findAllByGroupIds(groupIds: Set<GroupId>): Flux<GroupAggregate>
}