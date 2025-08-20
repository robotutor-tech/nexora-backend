package com.robotutor.nexora.modules.auth.adapters.persistance.repository

import com.robotutor.nexora.modules.auth.adapters.persistance.model.TokenDocument
import com.robotutor.nexora.modules.auth.domain.model.Token
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface TokenDocumentRepository : ReactiveCrudRepository<TokenDocument, String> {
    fun findByValue(value: String): Mono<TokenDocument>
    fun deleteByTokenId(tokenId: String): Mono<TokenDocument>
}