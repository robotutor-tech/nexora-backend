package com.robotutor.nexora.context.user.infrastructure.persistence.repository

import com.robotutor.nexora.context.user.domain.aggregate.UserAggregate
import com.robotutor.nexora.context.user.domain.repository.UserRepository
import com.robotutor.nexora.context.user.domain.vo.Email
import com.robotutor.nexora.context.user.domain.vo.UserId
import com.robotutor.nexora.context.user.infrastructure.persistence.document.UserDocument
import com.robotutor.nexora.context.user.infrastructure.persistence.mapper.UserDocumentMapper
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.infrastructure.persistence.repository.MongoRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MongoUserRepository(
    mongoTemplate: ReactiveMongoTemplate,
) : MongoRepository<UserAggregate, UserDocument>(mongoTemplate, UserDocument::class.java, UserDocumentMapper),
    UserRepository {
    override fun save(userAggregate: UserAggregate): Mono<UserAggregate> {
        val query = Query(Criteria.where("userId").`is`(userAggregate.userId.value))
        return this.findAndReplace(query, userAggregate)
    }

    override fun deleteByUserId(userId: UserId): Mono<UserAggregate> {
        val query = Query(Criteria.where("userId").`is`(userId.value))
        return this.deleteOne(query)
    }

    override fun findByUserId(userId: UserId): Mono<UserAggregate> {
        val query = Query(Criteria.where("userId").`is`(userId.value))
        return this.findOne(query)
    }

    override fun findByEmail(email: Email): Mono<UserAggregate> {
        val query = Query(Criteria.where("email").`is`(email.value))
        return this.findOne(query)
    }

    override fun findByAccountId(accountId: AccountId): Mono<UserAggregate> {
        val query = Query(Criteria.where("accountId").`is`(accountId.value))
        return this.findOne(query)
    }
}