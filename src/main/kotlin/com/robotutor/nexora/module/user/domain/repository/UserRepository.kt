package com.robotutor.nexora.module.user.domain.repository

import com.robotutor.nexora.module.user.domain.aggregate.User
import com.robotutor.nexora.module.user.domain.vo.Email
import com.robotutor.nexora.shared.domain.vo.UserId
import reactor.core.publisher.Mono

interface UserRepository {
    fun save(user: User): Mono<User>
    fun deleteByUserId(userId: UserId): Mono<User>
    fun findByUserId(userId: UserId): Mono<User>
    fun findByEmail(email: Email): Mono<User>
    fun existsByEmail(email: Email): Mono<Boolean>
}
