package com.robotutor.nexora.auth.models

import com.robotutor.nexora.auth.controllers.views.PremisesActorRequest
import com.robotutor.nexora.auth.models.TokenIdentifierType.*
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.gateway.view.ActorType
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.security.models.UserId
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
    val metadata: TokenMetaData,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val expiresOn: LocalDateTime
) {
    fun generatePremisesActorToken(tokenId: TokenId, premisesActorRequest: PremisesActorRequest): Token {
        return Token(
            tokenId = tokenId,
            value = generateTokenValue(),
            metadata = TokenMetaData(
                identifier = premisesActorRequest.actorId,
                identifierType = PREMISES_ACTOR,
                premisesId = premisesActorRequest.premisesId
            ),
            expiresOn = expiresOn
        )
    }

    companion object {
        fun generateAuthUser(tokenId: TokenId, userId: UserId): Token {
            return Token(
                tokenId = tokenId,
                value = generateTokenValue(),
                metadata = TokenMetaData(identifier = userId, identifierType = AUTH_USER),
                expiresOn = LocalDateTime.now().plusDays(7),
            )
        }

        fun generateInvitationToken(tokenId: TokenId, invitation: Invitation, userData: PremisesActorData): Token {
            return Token(
                tokenId = tokenId,
                value = generateTokenValue(),
                metadata = TokenMetaData(
                    identifier = invitation.invitationId,
                    identifierType = INVITATION,
                    premisesId = userData.premisesId
                ),
                expiresOn = LocalDateTime.now().plusDays(1)
            )
        }
    }
}

data class TokenMetaData(
    val identifier: String,
    val identifierType: TokenIdentifierType,
    val premisesId: PremisesId? = null,
    val actor: ActorData? = null
)

data class ActorData(val actorId: String, val premisesId: String, val type: ActorType)

enum class ActorType {
    HUMAN,
    DEVICE,
    LOCAL_SERVER,
    SERVER
}

enum class TokenIdentifierType {
    AUTH_USER,
    PREMISES_ACTOR,
    INVITATION;
}

private fun generateTokenValue(length: Int = 120): String {
    val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9') + "_-".split("")
    val token = List(length + 10) { chars.random() }.joinToString("").substring(0, length)
    return token + LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toString()
}

typealias TokenId = String
