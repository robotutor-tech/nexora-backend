package com.robotutor.nexora.module.identity.domain.repository

import com.robotutor.nexora.module.identity.domain.aggregate.Group
import com.robotutor.nexora.module.identity.domain.vo.GroupId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface GroupRepository {
    fun save(group: Group): Mono<Group>
    fun saveAll(groups: List<Group>): Flux<Group>
    fun findAllByGroupIds(groupIds: Set<GroupId>): Flux<Group>
}
