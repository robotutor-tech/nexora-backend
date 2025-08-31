package com.robotutor.nexora.modules.auth.infrastructure.persistence.repository

import com.robotutor.nexora.modules.auth.domain.model.Token
import com.robotutor.nexora.modules.auth.domain.model.TokenId
import com.robotutor.nexora.modules.auth.domain.repository.TokenRepository
import com.robotutor.nexora.modules.auth.infrastructure.persistence.mapper.TokenDocumentMapper
import com.robotutor.nexora.modules.auth.infrastructure.persistence.document.TokenDocument
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
) : MongoRepository<Token, TokenDocument>(mongoTemplate, TokenDocument::class.java, TokenDocumentMapper()),
    TokenRepository {
    override fun save(token: Token): Mono<Token> {
        val query = Query(Criteria.where("tokenId").`is`(token.tokenId.value))
        return this.findAndReplace(query, token)
    }

    override fun findByTokenId(tokenId: TokenId): Mono<Token> {
        val query = Query(Criteria.where("tokenId").`is`(tokenId.value))
        return this.findOne(query)
    }

    override fun findAllByTokenIdIn(tokenIds: List<TokenId>): Flux<Token> {
        val query = Query(Criteria.where("tokenId").`in`(tokenIds.map { it.value }))
        return this.findAll(query)
    }

    override fun findByValueAndExpiredAtAfter(tokenValue: String, expiresAt: Instant): Mono<Token> {
        val query = Query(
            Criteria.where("value").`is`(tokenValue)
                .and("expiresAt").gt(expiresAt)
        )
        return this.findOne(query)
    }
}