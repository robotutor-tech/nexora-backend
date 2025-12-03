package com.robotutor.nexora.context.user.infrastructure.persistence.repository

import com.robotutor.nexora.context.user.domain.repository.UserIdGenerator
import com.robotutor.nexora.context.user.infrastructure.persistence.document.IdType
import com.robotutor.nexora.shared.domain.model.UserId
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MongoUserIdGenerator(private val idGeneratorService: IdGeneratorService) : UserIdGenerator {
    override fun generate(): Mono<UserId> {
        return idGeneratorService.generateId(IdType.USER_ID, UserId::class.java)
    }
}