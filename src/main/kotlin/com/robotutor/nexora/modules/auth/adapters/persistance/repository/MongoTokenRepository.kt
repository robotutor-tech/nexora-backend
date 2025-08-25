package com.robotutor.nexora.modules.auth.adapters.persistance.repository

import com.robotutor.nexora.modules.auth.adapters.persistance.model.TokenDocument
import com.robotutor.nexora.modules.auth.domain.model.Token
import com.robotutor.nexora.modules.auth.domain.model.TokenId
import com.robotutor.nexora.modules.auth.domain.repository.TokenRepository
import org.springframework.data.mongodb.core.FindAndReplaceOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono


@Repository
class MongoTokenRepository(
    private val mongoTemplate: ReactiveMongoTemplate,
) : TokenRepository {

    override fun save(token: Token): Mono<Token> {
        val tokenDocument = TokenDocument.from(token)
        val query = Query(Criteria.where("tokenId").`is`(tokenDocument.tokenId))
        return mongoTemplate.replace(query, tokenDocument, FindAndReplaceOptions().upsert())
            .map { token }
    }

    override fun findByValue(value: String): Mono<Token> {
        return mongoTemplate.findOne(Query(Criteria.where("value").`is`(value)), TokenDocument::class.java)
            .map { it.toDomainModel() }
    }

    override fun findByTokenId(tokenId: TokenId): Mono<Token> {
        return mongoTemplate.findOne(Query(Criteria.where("tokenId").`is`(tokenId.value)), TokenDocument::class.java)
            .map { it.toDomainModel() }
    }

}