package com.robotutor.nexora.context.iam.infrastructure.persistence.mapper


import com.robotutor.nexora.context.iam.domain.entity.Token
import com.robotutor.nexora.context.iam.domain.entity.TokenId
import com.robotutor.nexora.context.iam.domain.entity.TokenValue
import com.robotutor.nexora.context.iam.infrastructure.persistence.document.TokenDocument
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.PrincipalDocumentMapper

object TokenDocumentMapper : DocumentMapper<Token, TokenDocument> {
    override fun toMongoDocument(domain: Token): TokenDocument {
        return TokenDocument(
            tokenId = domain.tokenId.value,
            tokenType = domain.tokenType,
            value = domain.value.value,
            principalType = domain.principalType,
            principal = PrincipalDocumentMapper.toPrincipalDocument(domain.principal),
            issuedAt = domain.issuedAt,
            expiresAt = domain.expiresAt,
            otherToken = domain.otherTokenId?.value,
        )
    }

    override fun toDomainModel(document: TokenDocument): Token {
        return Token(
            tokenId = TokenId(document.tokenId),
            tokenType = document.tokenType,
            value = TokenValue(document.value),
            principalType = document.principalType,
            principal = PrincipalDocumentMapper.toPrincipalContext(document.principal),
            issuedAt = document.issuedAt,
            expiresAt = document.expiresAt,
            otherTokenId = document.otherToken?.let { TokenId(it) }
        )
    }
}