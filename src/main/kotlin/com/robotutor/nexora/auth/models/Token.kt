package com.robotutor.nexora.auth.models

import com.robotutor.nexora.auth.controllers.views.TokenRequest
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.UserId
import com.robotutor.nexora.security.models.UserPremisesData
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.time.ZoneOffset

const val TOKEN_COLLECTION = "tokens"

@TypeAlias("Token")
@Document(TOKEN_COLLECTION)
data class Token(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val tokenId: TokenId,
    @Indexed(unique = true)
    val value: String,
    val userId: UserId,
    val premisesId: PremisesId? = null,
    val type: TokenType = TokenType.PARTIAL,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val expiresOn: LocalDateTime = LocalDateTime.now().plusDays(7)
) {
    fun generateToken(tokenId: TokenId, tokenRequest: TokenRequest): Token {
        return Token(
            tokenId = tokenId,
            value = generateTokenValue(),
            userId = userId,
            premisesId = tokenRequest.premisesId,
            type = TokenType.FULL,
            expiresOn = expiresOn,
        )
    }

    companion object {
        fun from(tokenId: TokenId, userId: UserId): Token {
            return Token(
                tokenId = tokenId,
                value = generateTokenValue(),
                userId = userId,
            )
        }

        fun generateInvitationToken(tokenId: TokenId, userData: UserPremisesData): Token {
            return Token(
                tokenId = tokenId,
                value = generateTokenValue(),
                userId = userData.userId,
                premisesId = userData.premisesId,
                type = TokenType.FULL,
                expiresOn = LocalDateTime.now().plusDays(1)
            )
        }


    }
}

enum class TokenType {
    PARTIAL,
    FULL
}

private fun generateTokenValue(length: Int = 120): String {
    val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9') + "_-".split("")
    val token = List(length + 10) { chars.random() }.joinToString("").substring(0, length)
    return token + LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toString()
}

typealias TokenId = String
