package com.robotutor.nexora.context.iam.infrastructure.persistence.repository

import com.robotutor.nexora.context.iam.domain.aggregate.SessionAggregate
import com.robotutor.nexora.context.iam.domain.repository.SessionRepository
import com.robotutor.nexora.context.iam.domain.vo.SessionId
import com.robotutor.nexora.context.iam.domain.vo.TokenValue
import com.robotutor.nexora.context.iam.infrastructure.persistence.document.SessionDocument
import com.robotutor.nexora.context.iam.infrastructure.persistence.mapper.SessionDocumentMapper
import com.robotutor.nexora.shared.infrastructure.persistence.repository.MongoRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

@Component
class MongoSessionRepository(
    mongoTemplate: ReactiveMongoTemplate,
) : MongoRepository<SessionAggregate, SessionDocument>(mongoTemplate, SessionDocument::class.java, SessionDocumentMapper),
    SessionRepository {
    override fun save(sessionAggregate: SessionAggregate): Mono<SessionAggregate> {
        val query = Query(Criteria.where("sessionId").`is`(sessionAggregate.sessionId.value))
        return this.findAndReplace(query, sessionAggregate)
    }

    override fun findByTokenId(sessionId: SessionId): Mono<SessionAggregate> {
        val query = Query(Criteria.where("tokenId").`is`(sessionId.value))
        return this.findOne(query)
    }

    override fun findAllByTokenIdIn(sessionIds: List<SessionId>): Flux<SessionAggregate> {
        val query = Query(Criteria.where("tokenId").`in`(sessionIds.map { it.value }))
        return this.findAll(query)
    }

    override fun findByTokenValueAndExpiredAtAfter(tokenValue: TokenValue, expiresAt: Instant): Mono<SessionAggregate> {
        val query = Query(
            Criteria.where("value").`is`(tokenValue.value)
                .and("expiresAt").gt(expiresAt)
        )
        return this.findOne(query)
    }
}