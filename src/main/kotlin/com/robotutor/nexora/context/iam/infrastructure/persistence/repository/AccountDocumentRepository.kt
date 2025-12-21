package com.robotutor.nexora.context.iam.infrastructure.persistence.repository

import com.robotutor.nexora.context.iam.infrastructure.persistence.document.AccountDocument
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface AccountDocumentRepository : ReactiveCrudRepository<AccountDocument, String> {
    fun findByAccountId(accountId: String): Mono<AccountDocument>
    fun deleteByAccountId(accountId: String): Mono<AccountDocument>
    fun findByCredentials_CredentialId(credentialId: String): Mono<AccountDocument>
}