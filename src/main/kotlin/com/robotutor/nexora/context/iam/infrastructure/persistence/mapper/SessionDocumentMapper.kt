package com.robotutor.nexora.context.iam.infrastructure.persistence.mapper

import com.robotutor.nexora.context.iam.domain.aggregate.SessionAggregate
import com.robotutor.nexora.context.iam.domain.vo.*
import com.robotutor.nexora.context.iam.infrastructure.persistence.document.AccountPrincipalDocument
import com.robotutor.nexora.context.iam.infrastructure.persistence.document.ActorPrincipalDocument
import com.robotutor.nexora.context.iam.infrastructure.persistence.document.SessionDocument
import com.robotutor.nexora.context.iam.infrastructure.persistence.document.SessionPrincipalDocument
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper

object SessionDocumentMapper : DocumentMapper<SessionAggregate, SessionDocument> {
    override fun toMongoDocument(domain: SessionAggregate): SessionDocument {
        return SessionDocument(
            sessionId = domain.sessionId.value,
            sessionPrincipal = toSessionPrincipalDocument(domain.sessionPrincipal),
            refreshTokenHash = domain.refreshTokenHash.hashedValue,
            refreshCount = domain.refreshCount,
            status = domain.status,
            issuedAt = domain.issuedAt,
            lastRefreshAt = domain.lastRefreshAt,
            expiresAt = domain.expiresAt,
            version = domain.version
        )
    }

    override fun toDomainModel(document: SessionDocument): SessionAggregate {
        return SessionAggregate(
            sessionId = SessionId(document.sessionId),
            sessionPrincipal = toPrincipalContext(document.sessionPrincipal),
            refreshTokenHash = HashedTokenValue(document.refreshTokenHash),
            refreshCount = document.refreshCount,
            status = document.status,
            issuedAt = document.issuedAt,
            lastRefreshAt = document.lastRefreshAt,
            expiresAt = document.expiresAt,
            version = document.version
        )
    }

    private fun toSessionPrincipalDocument(principal: SessionPrincipal): SessionPrincipalDocument {
        return when (principal) {
            is AccountPrincipal -> AccountPrincipalDocument(principal.accountId.value, principal.accountType)
            is ActorPrincipal -> ActorPrincipalDocument(
                principal.accountPrincipal.accountId.value,
                principal.actorId.value,
                principal.premisesId.value,
                principal.accountPrincipal.accountType
            )
        }
    }

    private fun toPrincipalContext(principalDocument: SessionPrincipalDocument): SessionPrincipal {
        return when (principalDocument) {
            is AccountPrincipalDocument -> AccountPrincipal(
                AccountId(principalDocument.accountId),
                principalDocument.type
            )

            is ActorPrincipalDocument -> ActorPrincipal(
                AccountPrincipal(AccountId(principalDocument.accountId), principalDocument.type),
                ActorId(principalDocument.actorId),
                PremisesId(principalDocument.premisesId)
            )
        }
    }
}