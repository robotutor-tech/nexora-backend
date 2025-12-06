package com.robotutor.nexora.context.iam.infrastructure.persistence.mapper

import com.robotutor.nexora.context.iam.domain.aggregate.TokenAggregate
import com.robotutor.nexora.context.iam.domain.vo.*
import com.robotutor.nexora.context.iam.infrastructure.persistence.document.AccountTokenPrincipalDocument
import com.robotutor.nexora.context.iam.infrastructure.persistence.document.ActorTokenPrincipalDocument
import com.robotutor.nexora.context.iam.infrastructure.persistence.document.TokenDocument
import com.robotutor.nexora.context.iam.infrastructure.persistence.document.TokenPrincipalDocument
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper

object TokenDocumentMapper : DocumentMapper<TokenAggregate, TokenDocument> {
    override fun toMongoDocument(domain: TokenAggregate): TokenDocument {
        return TokenDocument(
            tokenId = domain.tokenId.value,
            tokenType = domain.tokenType,
            value = domain.value.value,
            principalType = domain.principalType,
            principal = toPrincipalDocument(domain.principal),
            issuedAt = domain.issuedAt,
            expiresAt = domain.expiresAt,
            otherToken = domain.otherTokenId?.value,
        )
    }

    override fun toDomainModel(document: TokenDocument): TokenAggregate {
        return TokenAggregate(
            tokenId = TokenId(document.tokenId),
            tokenType = document.tokenType,
            value = TokenValue(document.value),
            principalType = document.principalType,
            principal = toPrincipalContext(document.principal),
            issuedAt = document.issuedAt,
            expiresAt = document.expiresAt,
            otherTokenId = document.otherToken?.let { TokenId(it) }
        )
    }

    private fun toPrincipalDocument(principal: TokenPrincipalContext): TokenPrincipalDocument {
        return when (principal) {
            is AccountTokenPrincipalContext -> AccountTokenPrincipalDocument(principal.accountId.value, principal.type)
            is ActorTokenPrincipalContext -> ActorTokenPrincipalDocument(
                principal.actorId,
                principal.roleId
            )
        }
    }

    private fun toPrincipalContext(principalDocument: TokenPrincipalDocument): TokenPrincipalContext {
        return when (principalDocument) {
            is AccountTokenPrincipalDocument -> AccountTokenPrincipalContext(
                AccountId(principalDocument.accountId),
                principalDocument.type
            )

            is ActorTokenPrincipalDocument -> ActorTokenPrincipalContext(
                principalDocument.actorId,
                principalDocument.roleId
            )
        }
    }
}