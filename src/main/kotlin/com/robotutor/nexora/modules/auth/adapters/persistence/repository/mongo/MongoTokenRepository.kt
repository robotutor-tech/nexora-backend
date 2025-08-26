package com.robotutor.nexora.modules.auth.adapters.persistence.repository.mongo

import com.robotutor.nexora.modules.auth.adapters.persistence.repository.document.TokenDocumentRepository
import com.robotutor.nexora.modules.auth.domain.model.Token
import com.robotutor.nexora.modules.auth.domain.model.TokenId
import com.robotutor.nexora.modules.auth.domain.repository.TokenRepository
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MongoTokenRepository(
    private val documentRepository: TokenDocumentRepository
) : TokenRepository {

    override fun save(token: Token): Mono<Token> {
        val query = Query(Criteria.where("tokenId").`is`(token.tokenId.value))
        return documentRepository.findAndReplace(query, token)
    }

    override fun findByValue(value: String): Mono<Token> {
        return documentRepository.findOne(Query(Criteria.where("value").`is`(value)))
    }

    override fun findByTokenId(tokenId: TokenId): Mono<Token> {
        return documentRepository.findOne(Query(Criteria.where("tokenId").`is`(tokenId.value)))
    }

    override fun deleteByTokenId(tokenId: TokenId): Mono<Token> {
        return documentRepository.deleteOne(Query(Criteria.where("tokenId").`is`(tokenId.value)))
    }

}