package com.robotutor.nexora.modules.auth.domain.model

import java.time.Instant


data class Token(
    val tokenId: TokenId,
    val tokenType: TokenType,
    val value: String,
    val issuedAt: Instant,
    val expiresAt: Instant,
    val metadata: Map<String, String> = emptyMap()
)

data class TokenId(val value: String)
enum class TokenType {
    AUTHORIZATION, INVITATION, REFRESH, DEVICE, SERVER
}