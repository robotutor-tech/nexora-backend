package com.robotutor.nexora.auth.models

import com.robotutor.nexora.auth.controllers.views.PremisesActorRequest
import com.robotutor.nexora.auth.gateways.view.ActorView
import com.robotutor.nexora.security.models.ActorIdentifier
import com.robotutor.nexora.security.models.Identifier
import com.robotutor.nexora.security.models.InvitationData
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.security.models.TokenIdentifier
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
    val tokenIdentifier: Identifier<TokenIdentifier>,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val expiresOn: LocalDateTime
) {
    fun generatePremisesActorToken(tokenId: TokenId, actor: ActorView): Token {
        return Token(
            tokenId = tokenId,
            value = generateTokenValue(),
            tokenIdentifier = Identifier(actor.actorId, TokenIdentifier.PREMISES_ACTOR),
            expiresOn = expiresOn
        )
    }

    companion object {
        fun generateAuthUser(tokenId: TokenId, userId: UserId): Token {
            return Token(
                tokenId = tokenId,
                value = generateTokenValue(),
                tokenIdentifier = Identifier(userId, TokenIdentifier.AUTH_USER),
                expiresOn = LocalDateTime.now().plusDays(7),
            )
        }

        fun generateInvitationToken(tokenId: String, invitation: Invitation): Token {
            return Token(
                tokenId = tokenId,
                value = generateTokenValue(),
                tokenIdentifier = Identifier(invitation.invitationId, TokenIdentifier.INVITATION),
                expiresOn = LocalDateTime.now().plusHours(6),
            )
        }

        fun generateDeviceActorToken(tokenId: TokenId, actorRequest: PremisesActorRequest): Token {
            return Token(
                tokenId = tokenId,
                value = generateTokenValue(60),
                tokenIdentifier = Identifier(actorRequest.actorId, TokenIdentifier.PREMISES_ACTOR),
                expiresOn = LocalDateTime.now().plusYears(100),
            )
        }
    }
}

private fun generateTokenValue(length: Int = 120): String {
    val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9') + "_-".split("")
    val token = List(length + 10) { chars.random() }.joinToString("").substring(0, length)
    return token + LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toString()
}

typealias TokenId = String
