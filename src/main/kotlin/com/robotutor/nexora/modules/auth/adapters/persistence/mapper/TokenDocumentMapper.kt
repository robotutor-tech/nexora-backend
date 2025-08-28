package com.robotutor.nexora.modules.auth.adapters.persistence.mapper

import com.robotutor.nexora.modules.auth.adapters.persistence.model.TokenDocument
import com.robotutor.nexora.modules.auth.domain.model.Token
import com.robotutor.nexora.modules.auth.domain.model.TokenId
import com.robotutor.nexora.modules.auth.domain.model.TokenValue
import com.robotutor.nexora.shared.adapters.persistence.mapper.DocumentMapper
import com.robotutor.nexora.shared.interfaces.mapper.PrincipalContextMapper
import org.springframework.stereotype.Service

@Service
class TokenDocumentMapper : DocumentMapper<Token, TokenDocument> {
    override fun toMongoDocument(domain: Token): TokenDocument {
        return TokenDocument(
            tokenId = domain.tokenId.value,
            tokenType = domain.tokenType,
            value = domain.value.value,
            principalType = domain.principalType,
            principal = PrincipalContextMapper.toPrincipalContextResponse(domain.principal),
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
            principal = PrincipalContextMapper.toPrincipalContext(document.principal),
            issuedAt = document.issuedAt,
            expiresAt = document.expiresAt,
            otherTokenId = document.otherToken?.let { TokenId(it) }
        )
    }
}