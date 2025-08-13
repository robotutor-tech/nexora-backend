package com.robotutor.nexora.modules.user.domain.repository

import com.robotutor.nexora.modules.user.domain.model.Email
import com.robotutor.nexora.modules.user.domain.model.User
import com.robotutor.nexora.shared.domain.model.UserId
import reactor.core.publisher.Mono

interface UserRepository {
    fun save(user: User): Mono<User>
    fun existsByEmail(email: Email): Mono<Boolean>
    fun deleteByUserId(userId: UserId): Mono<User>
}