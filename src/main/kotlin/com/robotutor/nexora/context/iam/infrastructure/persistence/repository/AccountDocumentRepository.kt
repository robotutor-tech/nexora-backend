package com.robotutor.nexora.context.iam.infrastructure.persistence.repository

import com.robotutor.nexora.context.iam.domain.vo.CredentialKind
import com.robotutor.nexora.context.iam.infrastructure.persistence.document.AccountDocument
import com.robotutor.nexora.context.iam.infrastructure.persistence.document.SessionDocument
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.Instant

@Repository
interface AccountDocumentRepository : ReactiveCrudRepository<AccountDocument, String> {
    fun findByAccountId(accountId: String): Mono<AccountDocument>
    fun deleteByAccountId(accountId: String): Mono<AccountDocument>
    fun findByCredentials_CredentialIdAndCredentials_Kind(
        credentialId: String,
        kind: CredentialKind
    ): Mono<AccountDocument>

}