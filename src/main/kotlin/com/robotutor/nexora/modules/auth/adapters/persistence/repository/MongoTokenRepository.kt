package com.robotutor.nexora.modules.auth.adapters.persistence.repository

import com.robotutor.nexora.modules.auth.adapters.persistence.mapper.TokenDocumentMapper
import com.robotutor.nexora.modules.auth.adapters.persistence.model.TokenDocument
import com.robotutor.nexora.modules.auth.domain.model.Token
import com.robotutor.nexora.modules.auth.domain.model.TokenId
import com.robotutor.nexora.modules.auth.domain.repository.TokenRepository
import com.robotutor.nexora.shared.adapters.persistence.repository.MongoRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MongoTokenRepository(
    mongoTemplate: ReactiveMongoTemplate,
) : MongoRepository<Token, TokenDocument>(mongoTemplate, TokenDocument::class.java, TokenDocumentMapper()),
    TokenRepository {
    override fun save(token: Token): Mono<Token> {
        val query = Query(Criteria.where("tokenId").`is`(token.tokenId.value))
        return this.findAndReplace(query, token)
    }

    override fun findByValue(value: String): Mono<Token> {
        val query = Query(Criteria.where("value").`is`(value))
        return this.findOne(query)
    }

    override fun findByTokenId(tokenId: TokenId): Mono<Token> {
        val query = Query(Criteria.where("tokenId").`is`(tokenId.value))
        return this.findOne(query)
    }

    override fun deleteByTokenId(tokenId: TokenId): Mono<Token> {
        val query = Query(Criteria.where("tokenId").`is`(tokenId.value))
        return this.deleteOne(query)
    }
}
