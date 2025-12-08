package com.robotutor.nexora.context.iam.domain.repository

import com.robotutor.nexora.context.iam.domain.aggregate.GroupAggregate
import reactor.core.publisher.Mono

interface GroupRepository {
    fun save(groupAggregate: GroupAggregate): Mono<GroupAggregate>
}