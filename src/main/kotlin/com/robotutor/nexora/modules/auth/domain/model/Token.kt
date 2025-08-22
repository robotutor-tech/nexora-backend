package com.robotutor.nexora.modules.auth.domain.model

import com.robotutor.nexora.shared.domain.model.PrincipalContext
import java.time.Instant

data class Token(
    val tokenId: TokenId,
    val principalType: com.robotutor.nexora.shared.domain.model.TokenPrincipalType,
    val principal: PrincipalContext,
    val tokenType: TokenType,
    val value: TokenValue,
    val issuedAt: Instant,
    val expiresAt: Instant,
    val metadata: Map<String, Any> = emptyMap()
)

data class TokenId(val value: String)
data class TokenValue(val value: String) {
    companion object {
        fun generate(length: Int = 120): TokenValue {
            val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9') + "_-".split("")
            val token = List(length) { chars.random() }.joinToString("").substring(0)
            val fullToken = token + Instant.now().epochSecond.toString()
            return TokenValue(fullToken.substring(fullToken.length - length))
        }
    }
}

data class Tokens(val token: Token, val refreshToken: Token)

enum class TokenType {
    AUTHORIZATION, INVITATION, REFRESH, DEVICE, SERVER
}
