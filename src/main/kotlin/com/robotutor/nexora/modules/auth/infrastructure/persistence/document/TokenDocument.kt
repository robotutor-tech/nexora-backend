package com.robotutor.nexora.modules.auth.infrastructure.persistence.document


import com.robotutor.nexora.modules.auth.domain.model.Token
import com.robotutor.nexora.modules.auth.domain.model.TokenType
import com.robotutor.nexora.shared.domain.model.PrincipalContext
import com.robotutor.nexora.shared.domain.model.TokenPrincipalType
import com.robotutor.nexora.shared.infrastructure.persistence.model.MongoDocument
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val TOKEN_COLLECTION = "tokens"

@TypeAlias("Token")
@Document(TOKEN_COLLECTION)
data class TokenDocument(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val tokenId: String,
    @Indexed(unique = true)
    val value: String,
    val otherToken: String? = null,
    val principalType: TokenPrincipalType,
    val principal: PrincipalContextDocument,
    val tokenType: TokenType,
    val issuedAt: Instant,
    @Indexed(name = "expireAtIndex", expireAfter = "0s")
    val expiresAt: Instant,
    @Version
    val version: Long? = null
) : MongoDocument<Token>

sealed interface PrincipalContextDocument
sealed interface ActorPrincipalContextDocument : PrincipalContextDocument

data class UserContextDocument(val userId: String) : ActorPrincipalContextDocument
data class DeviceContextDocument(val deviceId: String) : ActorPrincipalContextDocument
data class InvitationContextDocument(val invitationId: String) : PrincipalContextDocument
data class InternalContextDocument(val value: String) : PrincipalContextDocument
data class ActorContextDocument(
    val actorId: String,
    val roleId: String,
    val context: ActorPrincipalContextDocument
) : PrincipalContextDocument
