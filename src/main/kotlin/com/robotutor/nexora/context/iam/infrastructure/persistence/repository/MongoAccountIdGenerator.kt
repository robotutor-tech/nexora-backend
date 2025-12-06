package com.robotutor.nexora.context.iam.infrastructure.persistence.repository

import com.robotutor.nexora.context.iam.domain.repository.AccountIdGenerator
import com.robotutor.nexora.context.iam.infrastructure.persistence.document.IdType
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import com.robotutor.nexora.shared.domain.vo.AccountId
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MongoAccountIdGenerator(
    private val idGeneratorService: IdGeneratorService
) : AccountIdGenerator {
    override fun generate(): Mono<AccountId> {
        return idGeneratorService.generateId(IdType.ACCOUNT_ID).map { AccountId(it) }
    }
}