package com.robotutor.nexora.module.user.domain.repository

import com.robotutor.nexora.module.user.domain.aggregate.UserAggregate
import com.robotutor.nexora.module.user.domain.vo.Email
import com.robotutor.nexora.module.user.domain.vo.UserId
import reactor.core.publisher.Mono

interface UserRepository {
    fun save(userAggregate: UserAggregate): Mono<UserAggregate>
    fun deleteByUserId(userId: UserId): Mono<UserAggregate>
    fun findByUserId(userId: UserId): Mono<UserAggregate>
    fun findByEmail(email: Email): Mono<UserAggregate>
    fun existsByEmail(email: Email): Mono<Boolean>
}