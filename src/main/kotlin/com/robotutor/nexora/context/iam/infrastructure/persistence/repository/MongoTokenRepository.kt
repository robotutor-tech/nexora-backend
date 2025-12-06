package com.robotutor.nexora.context.iam.infrastructure.persistence.repository

import com.robotutor.nexora.context.iam.domain.aggregate.TokenAggregate
import com.robotutor.nexora.context.iam.domain.repository.TokenRepository
import com.robotutor.nexora.context.iam.domain.vo.TokenId
import com.robotutor.nexora.context.iam.domain.vo.TokenValue
import com.robotutor.nexora.context.iam.infrastructure.persistence.document.TokenDocument
import com.robotutor.nexora.context.iam.infrastructure.persistence.mapper.TokenDocumentMapper
import com.robotutor.nexora.shared.infrastructure.persistence.repository.MongoRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

@Component
class MongoTokenRepository(
    mongoTemplate: ReactiveMongoTemplate,
) : MongoRepository<TokenAggregate, TokenDocument>(mongoTemplate, TokenDocument::class.java, TokenDocumentMapper),
    TokenRepository {
    override fun save(tokenAggregate: TokenAggregate): Mono<TokenAggregate> {
        val query = Query(Criteria.where("tokenId").`is`(tokenAggregate.tokenId.value))
        return this.findAndReplace(query, tokenAggregate)
    }

    override fun findByTokenId(tokenId: TokenId): Mono<TokenAggregate> {
        val query = Query(Criteria.where("tokenId").`is`(tokenId.value))
        return this.findOne(query)
    }

    override fun findAllByTokenIdIn(tokenIds: List<TokenId>): Flux<TokenAggregate> {
        val query = Query(Criteria.where("tokenId").`in`(tokenIds.map { it.value }))
        return this.findAll(query)
    }

    override fun findByValueAndExpiredAtAfter(tokenValue: TokenValue, expiresAt: Instant): Mono<TokenAggregate> {
        val query = Query(
            Criteria.where("value").`is`(tokenValue.value)
                .and("expiresAt").gt(expiresAt)
        )
        return this.findOne(query)
    }
}