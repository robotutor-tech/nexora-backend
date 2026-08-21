package com.robotutor.nexora.module.identity.infrastructure.persistence.repository

import com.robotutor.nexora.module.identity.infrastructure.persistence.document.AccountDocument
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface AccountDocumentRepository : ReactiveCrudRepository<AccountDocument, String> {
    fun findByAccountId(accountId: String): Mono<AccountDocument>
    fun findBySubjectId(subjectId: String): Mono<AccountDocument>
    fun deleteByAccountId(accountId: String): Mono<AccountDocument>
    fun findByCredential_CredentialId(credentialId: String): Mono<AccountDocument>
    fun existsByCredential_CredentialId(credentialId: String): Mono<Boolean>
}
